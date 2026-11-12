package com.example.quanly_rapchieuphim.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDetailStatResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private Integer memberPoint;
    private String memberRank;
    private Long totalTicketsAllTime;
    private Long totalProductsAllTime;
    private Double totalRevenueAllTime;
    private List<InvoiceHistoryResponse> invoiceHistory;
}