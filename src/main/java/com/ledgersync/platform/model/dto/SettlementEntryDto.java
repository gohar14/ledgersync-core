package com.ledgersync.platform.model.dto;

import java.math.BigDecimal;

public record SettlementEntryDto(
                String sourceProvider,
                String rrn,
                String authCode,
                String requestId,
                BigDecimal grossAmount,
                BigDecimal netAmount,
                String fileName,
                Integer rowNumber) {
}
