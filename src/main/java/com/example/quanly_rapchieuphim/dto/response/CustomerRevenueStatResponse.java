package com.example.quanly_rapchieuphim.dto.response;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRevenueStatResponse {
    private UUID memberId;
    private String fullName;
    private String phoneNumber;
    private String memberRank;
    private Long totalTickets;
    private Long totalProducts;
    private Double totalRevenue;
}