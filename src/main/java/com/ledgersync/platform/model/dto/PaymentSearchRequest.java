package com.ledgersync.platform.model.dto;

import java.time.LocalDate;

public record PaymentSearchRequest(
                LocalDate startDate,
                LocalDate endDate,
                String gatewayReference,
                String status,
                java.math.BigDecimal minAmount) {
}
