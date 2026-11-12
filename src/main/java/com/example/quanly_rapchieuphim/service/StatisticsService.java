package com.example.quanly_rapchieuphim.service;

import com.example.quanly_rapchieuphim.dto.response.*;
import com.example.quanly_rapchieuphim.entity.Invoice;
import com.example.quanly_rapchieuphim.entity.InvoiceProduct;
import com.example.quanly_rapchieuphim.entity.Member;
import com.example.quanly_rapchieuphim.entity.User;
import com.example.quanly_rapchieuphim.exception.AppException;
import com.example.quanly_rapchieuphim.exception.ErrorCode;
import com.example.quanly_rapchieuphim.repository.InvoiceRepository;
import com.example.quanly_rapchieuphim.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public CustomerRevenueDashboardResponse getCustomerRevenueStatistics(LocalDate startDate, LocalDate endDate) {

        // Lay user
        Map<UUID, CustomerRevenueStatResponse> memberStatMap = new HashMap<>();
        List<User> allUsers = userRepository.findAll();

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        //Lay Invoice
        List<Invoice> invoices = invoiceRepository.findInvoicesByDateRange(startDateTime, endDateTime);

        long overallTickets = 0;
        long overallProducts = 0;
        double overallRevenue = 0.0;

        long guestTickets = 0;
        long guestProducts = 0;
        double guestRevenue = 0.0;


        for (User user : allUsers) {
            if (user instanceof Member member) {
                memberStatMap.put(member.getId(), CustomerRevenueStatResponse.builder()
                        .memberId(member.getId())
                        .fullName(member.getFullName())
                        .phoneNumber(member.getPhoneNumber())
                        .memberRank(member.getMemberRank() != null ? member.getMemberRank().name() : null)
                        .totalTickets(0L)
                        .totalProducts(0L)
                        .totalRevenue(0.0)
                        .build());
            }
        }

        // Duyet invoice
        for (Invoice invoice : invoices) {
            long ticketCount = invoice.getInvoiceTickets() != null ? invoice.getInvoiceTickets().size() : 0;
            long productCount = invoice.getInvoiceProducts() != null
                    ? invoice.getInvoiceProducts().stream().mapToLong(InvoiceProduct::getQuantity).sum()
                    : 0;
            double revenue = invoice.getTotalPayment() != null ? invoice.getTotalPayment() : 0.0;

            overallTickets += ticketCount;
            overallProducts += productCount;
            overallRevenue += revenue;

            Member member = invoice.getMember();

            //khach vang lai
            if (member == null) {
                guestTickets += ticketCount;
                guestProducts += productCount;
                guestRevenue += revenue;
            } else {
                CustomerRevenueStatResponse stat = memberStatMap.get(member.getId());
                if (stat != null) {
                    stat.setTotalTickets(stat.getTotalTickets() + ticketCount);
                    stat.setTotalProducts(stat.getTotalProducts() + productCount);
                    stat.setTotalRevenue(stat.getTotalRevenue() + revenue);
                }
            }
        }

        // Sap xep theo doanh thu
        List<CustomerRevenueStatResponse> sortedMemberStats = memberStatMap.values().stream()
                .sorted(Comparator.comparingDouble(CustomerRevenueStatResponse::getTotalRevenue).reversed())
                .collect(Collectors.toList());

        return CustomerRevenueDashboardResponse.builder()
                .overallSummary(RevenueSummaryGroupResponse.builder()
                        .totalTickets(overallTickets)
                        .totalProducts(overallProducts)
                        .totalRevenue(overallRevenue)
                        .build())
                .guestSummary(RevenueSummaryGroupResponse.builder()
                        .totalTickets(guestTickets)
                        .totalProducts(guestProducts)
                        .totalRevenue(guestRevenue)
                        .build())
                .memberStatistics(sortedMemberStats)
                .build();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public CustomerDetailStatResponse getCustomerDetailStatistics(UUID memberId) {
        //lay thong tin user
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        //kiem tra member
        if (!(user instanceof Member member)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        //lay tat ca hoa don khach hang
        List<Invoice> allTimeInvoices = invoiceRepository.findByMemberIdOrderByPaymentDateDesc(memberId);

        long totalTicketsAllTime = 0;
        long totalProductsAllTime = 0;
        double totalRevenueAllTime = 0.0;
        List<InvoiceHistoryResponse> historyList = new ArrayList<>();

        //tinh tong tien, ve, sp, tao lich su hoa don
        for (Invoice invoice : allTimeInvoices) {
            int ticketCount = invoice.getInvoiceTickets() != null ? invoice.getInvoiceTickets().size() : 0;
            int productCount = invoice.getInvoiceProducts() != null
                    ? invoice.getInvoiceProducts().stream().mapToInt(InvoiceProduct::getQuantity).sum()
                    : 0;
            double totalAmount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0.0;
            double discountAmount = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : 0.0;
            double totalPayment = invoice.getTotalPayment() != null ? invoice.getTotalPayment() : 0.0;

            totalTicketsAllTime += ticketCount;
            totalProductsAllTime += productCount;
            totalRevenueAllTime += totalPayment;

            historyList.add(InvoiceHistoryResponse.builder()
                    .paymentDate(invoice.getPaymentDate())
                    .paymentMethod(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod().name() : null)
                    .ticketCount(ticketCount)
                    .productCount(productCount)
                    .totalAmount(totalAmount)
                    .discountAmount(discountAmount)
                    .totalPayment(totalPayment)
                    .build());
        }

        return CustomerDetailStatResponse.builder()
                .fullName(member.getFullName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .birthDate(member.getDate())
                .memberPoint(member.getMemberPoint())
                .memberRank(member.getMemberRank() != null ? member.getMemberRank().name() : null)
                .totalTicketsAllTime(totalTicketsAllTime)
                .totalProductsAllTime(totalProductsAllTime)
                .totalRevenueAllTime(totalRevenueAllTime)
                .invoiceHistory(historyList)
                .build();
    }
}