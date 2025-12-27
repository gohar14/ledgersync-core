package com.ledgersync.platform.repository;

import com.ledgersync.platform.model.JobExecutionLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, UUID> {
    JobExecutionLog findByBatchId(Long batchId);
}
