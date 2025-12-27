package com.ledgersync.platform.model.dto;

import java.math.BigDecimal;

public record ExceptionDetailDto(
        Long batchId,
        String fileName,
        Integer rowNumber,
        String rrn,
        BigDecimal amount,
        String reason) {
}
