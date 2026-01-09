package com.ledgersync.platform.batch;

import com.ledgersync.platform.model.JobExecutionLog;
import com.ledgersync.platform.model.SettlementEntry;
import com.ledgersync.platform.repository.JobExecutionLogRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

@Component
public class JobIntegrityListener implements JobExecutionListener, ItemWriteListener<SettlementEntry> {

    private final JobExecutionLogRepository repository;

    // State per job execution (Note: This component must be Step/Job scoped if
    // running parallel jobs,
    // but for simplicity assuming single-threaded or handled via JobExecution
    // context)
    // Better approach: Store partial results in JobExecution context

    private BigDecimal totalNetAmount = BigDecimal.ZERO;

    public JobIntegrityListener(JobExecutionLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobExecutionLog log = new JobExecutionLog();
        log.setBatchId(jobExecution.getJobInstance().getInstanceId());
        log.setStartTime(LocalDateTime.now());
        log.setStatus("STARTED");

        String filePath = jobExecution.getJobParameters().getString("filePath");
        log.setFileName(filePath != null ? filePath : "UNKNOWN");
        log.setSourceProvider("HBL"); // Default for now

        repository.save(log);
        totalNetAmount = BigDecimal.ZERO; // Reset
    }

    @Override
    public void afterWrite(Chunk<? extends SettlementEntry> items) {
        for (SettlementEntry item : items) {
            if (item.getNetAmount() != null) {
                totalNetAmount = totalNetAmount.add(item.getNetAmount());
            }
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobExecutionLog log = repository.findByBatchId(jobExecution.getJobInstance().getInstanceId());
        if (log != null) {
            log.setEndTime(LocalDateTime.now());
            log.setStatus(jobExecution.getStatus().name());

            // Get stats from step executions
            long totalWriteCount = jobExecution.getStepExecutions().stream()
                    .mapToLong(step -> step.getWriteCount())
                    .sum();

            log.setRowsProcessed((int) totalWriteCount);
            log.setTotalRowsFound((int) totalWriteCount); // Assuming strict 1:1 for now
            log.setTotalNetAmountSum(totalNetAmount); // From listener accumulator

            repository.save(log);
        }
    }
}
