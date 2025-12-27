package com.ledgersync.platform.service.impl;

import com.ledgersync.platform.model.PaymentLog;
import com.ledgersync.platform.model.ReconResult;
import com.ledgersync.platform.model.SettlementEntry;
import com.ledgersync.platform.repository.PaymentLogRepository;
import com.ledgersync.platform.repository.ReconResultRepository;
import com.ledgersync.platform.repository.SettlementEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReconciliationServiceImplTest {

    @Mock
    private SettlementEntryRepository settlementEntryRepository;
    @Mock
    private PaymentLogRepository paymentLogRepository;
    @Mock
    private ReconResultRepository reconResultRepository;

    private ReconciliationServiceImpl reconciliationService;

    @BeforeEach
    void setUp() {
        reconciliationService = new ReconciliationServiceImpl(settlementEntryRepository, paymentLogRepository,
                reconResultRepository);
    }

    @Test
    void testFallbackMatching() {
        // Given
        UUID settlementId = UUID.randomUUID();
        SettlementEntry settlement = new SettlementEntry();
        settlement.setId(settlementId);
        settlement.setAuthCode("AUTH123");
        settlement.setGrossAmount(new BigDecimal("100.00"));

        PaymentLog paymentLog = new PaymentLog();
        paymentLog.setInternalId(UUID.randomUUID());
        paymentLog.setGatewayReference("AUTH123");
        paymentLog.setAmount(new BigDecimal("100.00"));

        when(settlementEntryRepository.findById(settlementId)).thenReturn(Optional.of(settlement));
        when(paymentLogRepository.findByGatewayReference("AUTH123")).thenReturn(Optional.of(paymentLog));

        // When
        reconciliationService.performMatch(settlementId);

        // Then
        ArgumentCaptor<ReconResult> captor = ArgumentCaptor.forClass(ReconResult.class);
        verify(reconResultRepository).save(captor.capture());
        assertEquals("MATCHED", captor.getValue().getMatchStatus());
    }

    @Test
    void testVarianceTolerance() {
        // Given
        UUID settlementId = UUID.randomUUID();
        SettlementEntry settlement = new SettlementEntry();
        settlement.setId(settlementId);
        settlement.setRrn("RRN123");
        settlement.setGrossAmount(new BigDecimal("100.00"));

        PaymentLog paymentLog = new PaymentLog();
        paymentLog.setInternalId(UUID.randomUUID());
        paymentLog.setGatewayReference("RRN123");
        paymentLog.setAmount(new BigDecimal("100.04")); // 0.04 difference, within 0.05 tolerance

        when(settlementEntryRepository.findById(settlementId)).thenReturn(Optional.of(settlement));
        when(paymentLogRepository.findByGatewayReference("RRN123")).thenReturn(Optional.of(paymentLog));

        // When
        reconciliationService.performMatch(settlementId);

        // Then
        ArgumentCaptor<ReconResult> captor = ArgumentCaptor.forClass(ReconResult.class);
        verify(reconResultRepository).save(captor.capture());
        assertEquals("MATCHED", captor.getValue().getMatchStatus());
    }

    @Test
    void testVarianceExceeded() {
        // Given
        UUID settlementId = UUID.randomUUID();
        SettlementEntry settlement = new SettlementEntry();
        settlement.setId(settlementId);
        settlement.setRrn("RRN123");
        settlement.setGrossAmount(new BigDecimal("100.00"));

        PaymentLog paymentLog = new PaymentLog();
        paymentLog.setInternalId(UUID.randomUUID());
        paymentLog.setGatewayReference("RRN123");
        paymentLog.setAmount(new BigDecimal("100.06")); // 0.06 difference, exceeds 0.05 tolerance

        when(settlementEntryRepository.findById(settlementId)).thenReturn(Optional.of(settlement));
        when(paymentLogRepository.findByGatewayReference("RRN123")).thenReturn(Optional.of(paymentLog));

        // When
        reconciliationService.performMatch(settlementId);

        // Then
        ArgumentCaptor<ReconResult> captor = ArgumentCaptor.forClass(ReconResult.class);
        verify(reconResultRepository).save(captor.capture());
        assertEquals("VARIANCE", captor.getValue().getMatchStatus());
    }
}
