package com.ledgersync.platform.service.impl;

import com.ledgersync.platform.model.JobExecutionLog;
import com.ledgersync.platform.model.SettlementEntry;
import com.ledgersync.platform.model.dto.ExceptionDetailDto;
import com.ledgersync.platform.model.dto.JobSummaryDto;
import com.ledgersync.platform.repository.JobExecutionLogRepository;
import com.ledgersync.platform.repository.SettlementEntryRepository;
import com.ledgersync.platform.service.JobMonitoringService;
import java.text.NumberFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobMonitoringServiceImpl implements JobMonitoringService {

    private final JobExecutionLogRepository jobLogRepository;
    private final SettlementEntryRepository settlementEntryRepository;

    public JobMonitoringServiceImpl(JobExecutionLogRepository jobLogRepository,
            SettlementEntryRepository settlementEntryRepository) {
        this.jobLogRepository = jobLogRepository;
        this.settlementEntryRepository = settlementEntryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryDto> getJobSummaries(Pageable pageable) {
        Page<JobExecutionLog> logs = jobLogRepository.findAll(pageable);

        return logs.map(log -> {
            Integer totalLoaded = log.getTotalRowsFound();

            // Calculate matched count (this is expensive in loop, optimization: store
            // matched count in Log or perform bulk aggregation)
            // For MVP, counting by file name which is indexed (wait, file_name IS NOT
            // INDEXED yet, only rrn/request_id).
            // Re-using the fileName from the log to find entries.
            // WARNING: Performance risk.
            // Better approach: Count distinct matched entries linked to this batch.
            // Since we don't strictly link SettlementEntry to BatchId, we rely on FileName
            // which was added.

            long matchedCount = settlementEntryRepository.countByFileNameAndReconStatus(log.getFileName(), "MATCHED");

            String rate = "0%";
            if (totalLoaded != null && totalLoaded > 0) {
                double matchRate = (double) matchedCount / totalLoaded;
                rate = NumberFormat.getPercentInstance().format(matchRate);
            }

            return new JobSummaryDto(
                    log.getBatchId(),
                    log.getFileName(),
                    log.getStatus(),
                    totalLoaded,
                    (int) matchedCount,
                    rate,
                    log.getStartTime());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExceptionDetailDto> getJobExceptions(Long batchId, Pageable pageable) {
        JobExecutionLog log = jobLogRepository.findByBatchId(batchId);
        if (log == null) {
            throw new IllegalArgumentException("Batch ID not found: " + batchId);
        }

        Page<SettlementEntry> exceptions = settlementEntryRepository.findByFileNameAndReconStatus(
                log.getFileName(), "UNMATCHED", pageable);

        return exceptions.map(entry -> new ExceptionDetailDto(
                batchId,
                entry.getFileName(),
                entry.getRowNumber(),
                entry.getRrn(),
                entry.getGrossAmount(),
                "Reconciliation failed or not attempted"));
    }
}
