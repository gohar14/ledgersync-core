package com.ledgersync.platform.service.impl;

import com.ledgersync.platform.model.PaymentLog;
import com.ledgersync.platform.model.SettlementEntry;
import com.ledgersync.platform.model.dto.DashboardStatsDto;
import com.ledgersync.platform.model.dto.PaymentSearchRequest;
import com.ledgersync.platform.repository.PaymentLogRepository;
import com.ledgersync.platform.repository.SettlementEntryRepository;
import com.ledgersync.platform.service.SearchService;
import com.ledgersync.platform.specification.PaymentSpecifications;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchServiceImpl implements SearchService {

    private final PaymentLogRepository paymentLogRepository;
    private final SettlementEntryRepository settlementEntryRepository;

    public SearchServiceImpl(PaymentLogRepository paymentLogRepository,
            SettlementEntryRepository settlementEntryRepository) {
        this.paymentLogRepository = paymentLogRepository;
        this.settlementEntryRepository = settlementEntryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentLog> searchPayments(PaymentSearchRequest request, Pageable pageable) {
        Specification<PaymentLog> spec = Specification.where(null);

        if (request.startDate() != null || request.endDate() != null) {
            spec = spec.and(PaymentSpecifications.withDateRange(request.startDate(), request.endDate()));
        }

        if (request.gatewayReference() != null) {
            spec = spec.and(PaymentSpecifications.withGatewayReference(request.gatewayReference()));
        }

        return paymentLogRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        long totalProcessed = settlementEntryRepository.count();
        long matchedCount = settlementEntryRepository.countByReconStatus("MATCHED");

        String matchRate = "0%";
        if (totalProcessed > 0) {
            double rate = (double) matchedCount / totalProcessed;
            matchRate = NumberFormat.getPercentInstance().format(rate);
        }

        BigDecimal unmatchedAmount = settlementEntryRepository.sumNetAmountByStatus("UNMATCHED");
        if (unmatchedAmount == null) {
            unmatchedAmount = BigDecimal.ZERO;
        }

        BigDecimal totalFees = settlementEntryRepository.sumTotalFees();
        if (totalFees == null) {
            totalFees = BigDecimal.ZERO;
        }

        return new DashboardStatsDto(totalProcessed, matchRate, unmatchedAmount, totalFees);
    }
}
