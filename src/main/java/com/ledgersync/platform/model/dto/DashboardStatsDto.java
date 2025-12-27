package com.ledgersync.platform.model.dto;

import java.math.BigDecimal;

public record DashboardStatsDto(
        long totalProcessed,
        String matchRatePercentage,
        BigDecimal unmatchedAmount,
        BigDecimal totalFeesComputed) {
}
