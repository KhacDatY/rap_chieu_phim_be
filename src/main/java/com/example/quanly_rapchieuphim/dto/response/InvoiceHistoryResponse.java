package com.example.quanly_rapchieuphim.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceHistoryResponse {
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private Integer ticketCount;
    private Integer productCount;
    private Double totalAmount;
    private Double discountAmount;
    private Double totalPayment;
}