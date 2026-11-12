package com.example.quanly_rapchieuphim.controller;

import com.example.quanly_rapchieuphim.dto.request.ApiResponse;
import com.example.quanly_rapchieuphim.dto.response.CustomerDetailStatResponse;
import com.example.quanly_rapchieuphim.dto.response.CustomerRevenueDashboardResponse;
import com.example.quanly_rapchieuphim.service.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/statistics/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {
    private final StatisticsService statisticsService;


    @GetMapping
    public ApiResponse<CustomerRevenueDashboardResponse> getCustomerRevenueStatistics(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ApiResponse.<CustomerRevenueDashboardResponse>builder()
                .result(statisticsService.getCustomerRevenueStatistics(startDate, endDate))
                .build();
    }

    @GetMapping("/{memberId}")
    public ApiResponse<CustomerDetailStatResponse> getCustomerDetailStatistics(
            @PathVariable("memberId") UUID memberId) {

        return ApiResponse.<CustomerDetailStatResponse>builder()
                .result(statisticsService.getCustomerDetailStatistics(memberId))
                .build();
    }
}