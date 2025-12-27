package com.ledgersync.platform.model.dto;

import java.time.LocalDateTime;

public record JobSummaryDto(
        Long batchId,
        String fileName,
        String status,
        Integer totalRows,
        Integer matchedRows,
        String matchRate,
        LocalDateTime startTime) {
}
