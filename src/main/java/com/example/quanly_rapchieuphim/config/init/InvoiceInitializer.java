package com.example.quanly_rapchieuphim.config.init;

import com.example.quanly_rapchieuphim.entity.*;
import com.example.quanly_rapchieuphim.enums.PaymentMethod;
import com.example.quanly_rapchieuphim.enums.TicketStatus;
import com.example.quanly_rapchieuphim.enums.DiscountType;
import com.example.quanly_rapchieuphim.repository.*;
import com.example.quanly_rapchieuphim.service.MemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(9) // Chạy cuối cùng sau khi vé (Ticket) đã được khởi tạo
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceInitializer implements ApplicationRunner {

    InvoiceRepository invoiceRepository;
    TicketRepository ticketRepository;
    ProductRepository productRepository;
    DiscountRepository discountRepository;
    UserRepository userRepository;
    MemberService memberService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Kiểm tra và khởi tạo dữ liệu Invoice (Hóa đơn)...");

        // BƯỚC 1: KIỂM TRA TRẠNG THÁI DỮ LIỆU VÉ VÀ MỤC TIÊU BÁN HÀNG
        List<Ticket> allTickets = ticketRepository.findAll();
        if (allTickets.isEmpty()) {
            log.warn("Không có vé nào trong hệ thống để tạo hóa đơn.");
            return;
        }

        long currentPaidTickets = allTickets.stream().filter(t -> t.getStatus() == TicketStatus.PAID).count();
        int targetTickets = (int) (allTickets.size() * 0.6); // Mục tiêu tiêu thụ 60% kho vé

        if (currentPaidTickets >= targetTickets) {
            log.info("Dữ liệu Invoice đã đạt ngưỡng 60% vé ({}/{}). Bỏ qua khởi tạo.", currentPaidTickets, allTickets.size());
            return;
        }

        int ticketsToBuyThisTime = targetTickets - (int) currentPaidTickets;
        int soldTickets = 0;

        // BƯỚC 2: CHUẨN BỊ TÀI NGUYÊN DỮ LIỆU ĐỂ TẠO HÓA ĐƠN
        List<Member> members = userRepository.findAll().stream()
                .filter(u -> u instanceof Member).map(u -> (Member) u).collect(Collectors.toList());
        List<Product> products = productRepository.findAll();
        List<Discount> discounts = discountRepository.findAll();

        // Gom nhóm vé đang còn trống (AVAILABLE) theo từng Suất chiếu
        Map<ShowTime, List<Ticket>> ticketsByShowTime = new HashMap<>();
        for (Ticket t : allTickets) {
            if (t.getStatus() == TicketStatus.AVAILABLE) {
                ticketsByShowTime.computeIfAbsent(t.getShowTime(), k -> new ArrayList<>()).add(t);
            }
        }

        List<Invoice> invoicesToSave = new ArrayList<>();
        List<Ticket> ticketsToUpdate = new ArrayList<>();
        Set<Member> membersToUpdate = new HashSet<>();
        Random random = new Random();
        List<ShowTime> showTimes = new ArrayList<>(ticketsByShowTime.keySet());

        // BƯỚC 3: TIẾN HÀNH TẠO HÓA ĐƠN CHO ĐẾN KHI ĐẠT ĐỦ SỐ LƯỢNG VÉ MỤC TIÊU
        while (soldTickets < ticketsToBuyThisTime && !showTimes.isEmpty()) {

            // 3.1. Chọn ngẫu nhiên 1 suất chiếu và lấy danh sách ghế trống
            ShowTime randomShowTime = showTimes.get(random.nextInt(showTimes.size()));
            List<Ticket> availableTickets = ticketsByShowTime.get(randomShowTime);

            if (availableTickets.isEmpty()) {
                showTimes.remove(randomShowTime);
                continue;
            }

            // 3.2. Quyết định số lượng vé sẽ mua trong hóa đơn này (Tối đa 5 vé, không vượt quá số vé cần mua thêm)
            int maxCanBuy = Math.min(availableTickets.size(), ticketsToBuyThisTime - soldTickets);
            int numTicketsToBuy = Math.min(random.nextInt(5) + 1, maxCanBuy);

            Invoice invoice = new Invoice();

            // 3.3. Định danh khách hàng theo quy luật 80/20 (55% Hóa đơn thuộc về Member, 45% Khách vãng lai)
            Member buyer = null;
            if (random.nextDouble() < 0.55 && !members.isEmpty()) {
                buyer = pickWeightedMember(members, random);
                invoice.setMember(buyer);
            } else {
                invoice.setMember(null);
            }

            // 3.4. Cấu hình phương thức và thời gian thanh toán
            PaymentMethod[] pMethods = PaymentMethod.values();
            invoice.setPaymentMethod(pMethods[random.nextInt(pMethods.length)]);
            invoice.setPaymentDate(randomShowTime.getStartDate().minusHours(random.nextInt(72) + 1));

            double subTotal = 0.0;

            // 3.5. Xử lý logic mua Vé
            for (int i = 0; i < numTicketsToBuy; i++) {
                Ticket ticket = availableTickets.remove(0);
                ticket.setStatus(TicketStatus.PAID);
                ticketsToUpdate.add(ticket);

                InvoiceTicket invoiceTicket = InvoiceTicket.builder()
                        .price(ticket.getTicketPrice().getPrice()) // Chốt giá vé tại thời điểm bán
                        .ticket(ticket)
                        .build();

                invoice.getInvoiceTickets().add(invoiceTicket);
                subTotal += invoiceTicket.getPrice();
            }

            // 3.6. Xử lý logic mua Sản phẩm (Bắp, Nước)
            int numProductsToBuy = random.nextInt(4); // Có thể không mua (0), hoặc mua tới 3 loại
            for (int i = 0; i < numProductsToBuy; i++) {
                Product product = products.get(random.nextInt(products.size()));
                int quantity = random.nextInt(3) + 1; // Số lượng từ 1 đến 3 phần

                InvoiceProduct invoiceProduct = InvoiceProduct.builder()
                        .product(product)
                        .quantity(quantity)
                        .price(product.getPrice()) // Chốt giá sản phẩm tại thời điểm bán
                        .build();

                invoice.getInvoiceProducts().add(invoiceProduct);
                subTotal += (invoiceProduct.getPrice() * quantity);
            }

            // 3.7. Xử lý logic áp dụng Mã giảm giá (Discount)
            double discountAmount = 0.0;
            if (random.nextBoolean() && !discounts.isEmpty()) { // Tỷ lệ 50% thử nghiệm
                Discount discount = discounts.get(random.nextInt(discounts.size()));
                // Kiểm tra điều kiện đơn hàng tối thiểu
                if (subTotal >= discount.getMinValue()) {
                    if (discount.getType() == DiscountType.PERCENTAGE) {
                        discountAmount = (subTotal * discount.getValue()) / 100;
                    } else {
                        discountAmount = discount.getValue();
                    }
                    // Giới hạn giảm giá tối đa
                    if (discount.getMaxDiscount() != null && discountAmount > discount.getMaxDiscount()) {
                        discountAmount = discount.getMaxDiscount();
                    }

                    InvoiceDiscount invoiceDiscount = InvoiceDiscount.builder()
                            .discount(discount)
                            .discountValue((int) discountAmount)
                            .build();

                    // Thêm vào danh sách quản lý của Invoice
                    if (invoice.getInvoiceDiscounts() == null) {
                        invoice.setInvoiceDiscounts(new ArrayList<>());
                    }
                    invoice.getInvoiceDiscounts().add(invoiceDiscount);
                    // ------------------------------------------
                }
            }

            // 3.8. Chốt tổng tiền Hóa đơn
            double finalTotalPayment = Math.max(0.0, subTotal - discountAmount);
            invoice.setTotalAmount(subTotal);           // Giá trước giảm
            invoice.setDiscountAmount(discountAmount);  // Tiền giảm giá
            invoice.setTotalPayment(finalTotalPayment);

            invoicesToSave.add(invoice);

            // 3.9. Tự động cộng điểm tích lũy và xét lên hạng cho Member (Nếu không phải khách vãng lai)
            if (buyer != null) {
                memberService.addPointsAndUpgradeRank(buyer, finalTotalPayment);
                membersToUpdate.add(buyer);
            }

            soldTickets += numTicketsToBuy;
        }

        // BƯỚC 4: LƯU VÀ ĐẨY DỮ LIỆU XUỐNG DATABASE NGAY LẬP TỨC
        invoiceRepository.saveAllAndFlush(invoicesToSave);
        ticketRepository.saveAllAndFlush(ticketsToUpdate);
        userRepository.saveAllAndFlush(membersToUpdate);

        log.info("==================================================================");
        log.info("HOÀN TẤT KHỞI TẠO HÓA ĐƠN THÀNH CÔNG!");
        log.info("- Tổng số hóa đơn vừa tạo : {} hóa đơn", invoicesToSave.size());
        log.info("- Tổng số vé đã bán (PAID): {} vé (~60% công suất rạp)", soldTickets);
        log.info("- Số thành viên tích điểm : {}/{} khách hàng", membersToUpdate.size(), members.size());
        log.info("==================================================================");
    }

    /**
     * Phân bổ khách hàng mua vé theo quy luật Pareto (80/20) trên tập 60 Member
     * Giúp tạo ra đầy đủ các phân khúc hạng: BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
     */
    private Member pickWeightedMember(List<Member> members, Random random) {
        int total = members.size();
        if (total < 10) return members.get(random.nextInt(total));

        int tier1VipLimit = (int) (total * 0.20);     // 20% đầu (12 người): Khách VIP nghiện phim -> Lên PLATINUM, DIAMOND
        int tier2GoldLimit = (int) (total * 0.40);    // 20% tiếp (12 người): Khách thân thiết -> Lên GOLD
        int tier3SilverLimit = (int) (total * 0.70);  // 30% tiếp (18 người): Khách tầm trung -> Lên SILVER (Bạc)
        // 30% cuối (18 người): Khách ít mua / mới tạo -> Giữ hạng BRONZE (Đồng)

        double roll = random.nextDouble();
        if (roll < 0.55) {
            // 55% lượng hóa đơn thành viên dồn cho 12 khách VIP
            return members.get(random.nextInt(tier1VipLimit));
        } else if (roll < 0.80) {
            // 25% lượng hóa đơn dành cho 12 khách nhóm Gold
            return members.get(tier1VipLimit + random.nextInt(tier2GoldLimit - tier1VipLimit));
        } else if (roll < 0.96) {
            // 16% lượng hóa đơn rải cho 18 khách nhóm Silver (mua 2-3 lần, đạt 500 - 1400 điểm)
            return members.get(tier2GoldLimit + random.nextInt(tier3SilverLimit - tier2GoldLimit));
        } else {
            // 4% lượng hóa đơn rơi vào 18 khách cuối (chỉ mua 1 đơn nhỏ hoặc chưa mua -> dưới 500 điểm, giữ hạng Đồng)
            return members.get(tier3SilverLimit + random.nextInt(total - tier3SilverLimit));
        }
    }
}

/**
 * =====================================================================================
 * TỔNG HỢP CÁC QUY TẮC SINH DỮ LIỆU VÉ, HÓA ĐƠN & TÍCH ĐIỂM (MOCK DATA RULES)
 * =====================================================================================
 *
 * 1. QUY TẮC MỤC TIÊU BÁN VÉ (TICKET COVERAGE TARGET):
 *    - Hệ thống đảm bảo lượng vé bán ra (PAID) chiếm chính xác 60% tổng số vé đã tạo,
 *      40% số vé còn lại giữ trạng thái AVAILABLE để phục vụ test đặt vé mới.
 *    - Thuật toán có tính toàn vẹn (Idempotent): Khi khởi động lại, hệ thống tự đếm số
 *      vé đã PAID, nếu đủ 60% sẽ tự động bỏ qua, nếu thiếu chỉ tạo bù phần còn thiếu.
 *
 * 2. QUY TẮC GOM NHÓM VÉ THEO SUẤT CHIẾU (SHOWTIME GROUPING):
 *    - Vé AVAILABLE được phân nhóm theo từng Suất chiếu (ShowTime).
 *    - Mỗi hóa đơn chỉ lấy từ 1 đến 5 vé thuộc cùng một suất chiếu để phản ánh đúng
 *      hành vi mua vé đi xem phim theo cá nhân hoặc hội nhóm.
 *
 * 3. QUY TẮC PHÂN LOẠI KHÁCH HÀNG & HÀNH VI MUA 80/20 (PARETO SEGMENTATION):
 *    - 45% Hóa đơn có member_id = null (Khách vãng lai mua trực tiếp tại quầy).
 *    - 55% Hóa đơn thuộc về Khách hàng thành viên (60 Member), được phân bổ theo trọng số:
 *        + 20% Member đầu (12 người): Chiếm 55% hóa đơn thành viên -> Đạt hạng PLATINUM, DIAMOND.
 *        + 20% Member tiếp (12 người): Chiếm 25% hóa đơn thành viên -> Đạt hạng GOLD.
 *        + 30% Member tiếp (18 người): Chiếm 16% hóa đơn thành viên -> Đạt hạng SILVER (Bạc).
 *        + 30% Member cuối (18 người): Chiếm 4% hóa đơn thành viên  -> Giữ hạng BRONZE (Đồng).
 *
 * 4. QUY TẮC GIỎ HÀNG & GIÁ LỊCH SỬ (CART & HISTORICAL PRICING):
 *    - Sản phẩm đi kèm (Bắp/Nước): Mỗi hóa đơn mua ngẫu nhiên từ 0 (không mua) đến 3
 *      loại sản phẩm khác nhau, mỗi loại từ 1 đến 3 phần.
 *    - Giá bán của từng vé và từng sản phẩm được sao chép và chốt cứng vào bảng
 *      InvoiceTicket và InvoiceProduct ngay tại thời điểm mua.
 *
 * 5. QUY TẮC ÁP DỤNG KHUYẾN MÃI (DISCOUNT APPLICATION):
 *    - 50% hóa đơn được chọn ngẫu nhiên để thử áp dụng mã giảm giá.
 *    - Mã chỉ được tính khi tổng tiền tạm tính (subTotal) đạt mức tối thiểu (minValue).
 *    - Hỗ trợ tính toán cho cả 2 hình thức: Giảm theo phần trăm (PERCENTAGE) và Giảm
 *      tiền mặt trực tiếp (FIXED_AMOUNT), đồng thời chặn trần mức giảm tối đa (maxDiscount).
 *
 * 6. QUY TẮC TÍCH ĐIỂM & THĂNG HẠNG THÀNH VIÊN (LOYALTY & RANKING RULE):
 *    - Giữ nguyên công thức: Cứ mỗi 1,000 VNĐ trên số tiền thực trả (totalAmount),
 *      thành viên được cộng 1 điểm tích lũy.
 *    - Tự động xét duyệt thăng hạng (MemberRank) ngay sau mỗi giao dịch theo các mốc:
 *        + BRONZE (Đồng)     : 0 - 499 điểm
 *        + SILVER (Bạc)      : 500 - 1,499 điểm
 *        + GOLD (Vàng)       : 1,500 - 2,999 điểm
 *        + PLATINUM (Bạch Kim): 3,000 - 4,999 điểm
 *        + DIAMOND (Kim Cương): Từ 5,000 điểm trở lên
 * =====================================================================================
 */