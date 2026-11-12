package com.example.quanly_rapchieuphim.dto.response;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRevenueDashboardResponse {
    private RevenueSummaryGroupResponse overallSummary; // Tổng tất cả hóa đơn
    private RevenueSummaryGroupResponse guestSummary;   // Tổng khách vãng lai (member_id = null)
    private List<CustomerRevenueStatResponse> memberStatistics; // Danh sách thành viên xếp giảm dần theo doanh thu
}