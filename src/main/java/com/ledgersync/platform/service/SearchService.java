package com.ledgersync.platform.service;

import com.ledgersync.platform.model.PaymentLog;
import com.ledgersync.platform.model.dto.DashboardStatsDto;
import com.ledgersync.platform.model.dto.PaymentSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    Page<PaymentLog> searchPayments(PaymentSearchRequest request, Pageable pageable);

    DashboardStatsDto getDashboardStats();
}
