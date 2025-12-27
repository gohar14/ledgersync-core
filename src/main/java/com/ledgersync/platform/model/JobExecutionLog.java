package com.ledgersync.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ls_job_execution_log")
public class JobExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "batch_id", nullable = false)
    private Long batchId; // Link to Spring Batch Job Instance ID

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "source_provider")
    private String sourceProvider;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "total_rows_found")
    private Integer totalRowsFound;

    @Column(name = "rows_processed")
    private Integer rowsProcessed;

    @Column(name = "total_net_amount_sum", precision = 19, scale = 4)
    private BigDecimal totalNetAmountSum;

    public JobExecutionLog() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSourceProvider() {
        return sourceProvider;
    }

    public void setSourceProvider(String sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalRowsFound() {
        return totalRowsFound;
    }

    public void setTotalRowsFound(Integer totalRowsFound) {
        this.totalRowsFound = totalRowsFound;
    }

    public Integer getRowsProcessed() {
        return rowsProcessed;
    }

    public void setRowsProcessed(Integer rowsProcessed) {
        this.rowsProcessed = rowsProcessed;
    }

    public BigDecimal getTotalNetAmountSum() {
        return totalNetAmountSum;
    }

    public void setTotalNetAmountSum(BigDecimal totalNetAmountSum) {
        this.totalNetAmountSum = totalNetAmountSum;
    }
}
