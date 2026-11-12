package com.example.quanly_rapchieuphim.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueSummaryGroupResponse {
    private Long totalTickets;
    private Long totalProducts;
    private Double totalRevenue;
}