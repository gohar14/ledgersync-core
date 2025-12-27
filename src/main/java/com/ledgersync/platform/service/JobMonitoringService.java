package com.ledgersync.platform.service;

import com.ledgersync.platform.model.dto.ExceptionDetailDto;
import com.ledgersync.platform.model.dto.JobSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobMonitoringService {
    Page<JobSummaryDto> getJobSummaries(Pageable pageable);

    Page<ExceptionDetailDto> getJobExceptions(Long batchId, Pageable pageable);
}
