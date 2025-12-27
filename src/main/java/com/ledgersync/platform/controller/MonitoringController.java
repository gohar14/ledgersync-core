package com.ledgersync.platform.controller;

import com.ledgersync.platform.model.dto.ExceptionDetailDto;
import com.ledgersync.platform.model.dto.JobSummaryDto;
import com.ledgersync.platform.service.JobMonitoringService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jobs")
public class MonitoringController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MonitoringController.class);

    private final JobMonitoringService monitoringService;

    public MonitoringController(JobMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/summary")
    public Page<JobSummaryDto> getJobSummary(Pageable pageable) {
        log.info("Fetching job summary");
        return monitoringService.getJobSummaries(pageable);
    }

    @GetMapping("/{batchId}/exceptions")
    public Page<ExceptionDetailDto> getJobExceptions(@PathVariable Long batchId, Pageable pageable) {
        log.info("Fetching exceptions for batch: {}", batchId);
        return monitoringService.getJobExceptions(batchId, pageable);
    }
}
