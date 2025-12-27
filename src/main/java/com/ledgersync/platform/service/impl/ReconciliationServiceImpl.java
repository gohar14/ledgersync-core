package com.ledgersync.platform.service.impl;

import com.ledgersync.platform.model.PaymentLog;
import com.ledgersync.platform.model.ReconResult;
import com.ledgersync.platform.model.SettlementEntry;
import com.ledgersync.platform.repository.PaymentLogRepository;
import com.ledgersync.platform.repository.ReconResultRepository;
import com.ledgersync.platform.repository.SettlementEntryRepository;
import com.ledgersync.platform.service.ReconciliationService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReconciliationServiceImpl implements ReconciliationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReconciliationServiceImpl.class);

    private final SettlementEntryRepository settlementEntryRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final ReconResultRepository reconResultRepository;

    public ReconciliationServiceImpl(SettlementEntryRepository settlementEntryRepository,
            PaymentLogRepository paymentLogRepository,
            ReconResultRepository reconResultRepository) {
        this.settlementEntryRepository = settlementEntryRepository;
        this.paymentLogRepository = paymentLogRepository;
        this.reconResultRepository = reconResultRepository;
    }

    @Override
    @Transactional
    public void performMatch(UUID settlementId) {
        log.info("Performing match for settlement entry: {}", settlementId);
        SettlementEntry settlement = settlementEntryRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found: " + settlementId));

        // Primary Match: Check gateway_reference in PaymentLog matches rrn or requestId
        // For simplicity assuming gatewayReference maps to RRN here.

        Optional<PaymentLog> paymentLogOpt = findPaymentLog(settlement);

        if (paymentLogOpt.isPresent()) {
            PaymentLog paymentLog = paymentLogOpt.get();
            matchAndSave(settlement, paymentLog);
        } else {
            log.warn("No match found for settlement: {}", settlementId);
            settlement.setReconStatus("UNMATCHED");
            settlementEntryRepository.save(settlement);
        }
    }

    private Optional<PaymentLog> findPaymentLog(SettlementEntry settlement) {
        // 1. Try RRN
        if (settlement.getRrn() != null) {
            Optional<PaymentLog> match = paymentLogRepository.findByGatewayReference(settlement.getRrn());
            if (match.isPresent())
                return match;
        }

        // 2. Try Auth Code
        if (settlement.getAuthCode() != null) {
            Optional<PaymentLog> match = paymentLogRepository.findByGatewayReference(settlement.getAuthCode());
            if (match.isPresent())
                return match;
        }

        // 3. Try Request ID
        if (settlement.getRequestId() != null) {
            return paymentLogRepository.findByGatewayReference(settlement.getRequestId());
        }

        return Optional.empty();
    }

    private void matchAndSave(SettlementEntry settlement, PaymentLog paymentLog) {
        BigDecimal variance = settlement.getGrossAmount().subtract(paymentLog.getAmount());

        // Tolerance check: e.g., 0.05
        BigDecimal tolerance = new BigDecimal("0.05");
        String status;

        if (variance.abs().compareTo(tolerance) <= 0) {
            status = "MATCHED";
        } else {
            status = "VARIANCE";
        }

        ReconResult result = new ReconResult(
                settlement.getId(),
                paymentLog.getInternalId(),
                status,
                variance);

        reconResultRepository.save(result);

        settlement.setReconStatus(status);
        settlementEntryRepository.save(settlement);

        log.info("Match result: {} for settlement {} and payment {} (Variance: {})",
                status, settlement.getId(), paymentLog.getInternalId(), variance);
    }
}
