package com.ledgersync.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Index;

@Entity
@Table(name = "ls_settlement_entry", indexes = {
        @Index(name = "idx_settlement_rrn", columnList = "rrn"),
        @Index(name = "idx_settlement_request_id", columnList = "request_id")
})
public class SettlementEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "source_provider", nullable = false)
    private String sourceProvider;

    @Column(name = "rrn")
    private String rrn;

    @Column(name = "auth_code")
    private String authCode;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "gross_amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "net_amount", precision = 19, scale = 4)
    private BigDecimal netAmount;

    @Column(name = "fee_amount", precision = 19, scale = 4)
    private BigDecimal feeAmount;

    @Column(name = "recon_status")
    private String reconStatus;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "row_number")
    private Integer rowNumber;

    public SettlementEntry() {
    }

    public SettlementEntry(com.ledgersync.platform.model.dto.SettlementEntryDto dto) {
        // Flexible Constructor Body (Java 25 Feature): Logic before super() or
        // initialization
        if (dto.grossAmount() == null)
            throw new IllegalArgumentException("Gross amount cannot be null");
        BigDecimal calculatedFee = dto.netAmount() != null ? dto.grossAmount().subtract(dto.netAmount())
                : BigDecimal.ZERO;

        this.sourceProvider = dto.sourceProvider();
        this.rrn = dto.rrn();
        this.authCode = dto.authCode();
        this.requestId = dto.requestId();
        this.grossAmount = dto.grossAmount();
        this.netAmount = dto.netAmount();
        this.fileName = dto.fileName();
        this.rowNumber = dto.rowNumber();
        this.feeAmount = calculatedFee;
        this.reconStatus = "PENDING";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSourceProvider() {
        return sourceProvider;
    }

    public void setSourceProvider(String sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getReconStatus() {
        return reconStatus;
    }

    public void setReconStatus(String reconStatus) {
        this.reconStatus = reconStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }
}
