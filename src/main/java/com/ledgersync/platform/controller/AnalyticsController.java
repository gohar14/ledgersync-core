package com.ledgersync.platform.controller;

import com.ledgersync.platform.model.PaymentLog;
import com.ledgersync.platform.model.dto.DashboardStatsDto;
import com.ledgersync.platform.model.dto.PaymentSearchRequest;
import com.ledgersync.platform.service.SearchService;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AnalyticsController {

    private final SearchService searchService;

    public AnalyticsController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/payments/search")
    public Page<PaymentLog> searchPayments(@RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String gatewayReference,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        PaymentSearchRequest request = new PaymentSearchRequest(startDate, endDate, gatewayReference, status);
        return searchService.searchPayments(request, pageable);
    }

    @GetMapping("/dashboard/stats")
    public DashboardStatsDto getDashboardStats() {
        return searchService.getDashboardStats();
    }
}
