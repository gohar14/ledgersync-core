package com.ledgersync.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ls_recon_result")
public class ReconResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "settlement_entry_id", nullable = false)
    private UUID settlementEntryId;

    @Column(name = "payment_log_id", nullable = false)
    private UUID paymentLogId;

    @Column(name = "match_status", nullable = false)
    private String matchStatus;

    @Column(name = "variance_amount", precision = 19, scale = 4)
    private BigDecimal varianceAmount;

    public ReconResult() {
    }

    public ReconResult(UUID settlementEntryId, UUID paymentLogId, String matchStatus, BigDecimal varianceAmount) {
        this.settlementEntryId = settlementEntryId;
        this.paymentLogId = paymentLogId;
        this.matchStatus = matchStatus;
        this.varianceAmount = varianceAmount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSettlementEntryId() {
        return settlementEntryId;
    }

    public void setSettlementEntryId(UUID settlementEntryId) {
        this.settlementEntryId = settlementEntryId;
    }

    public UUID getPaymentLogId() {
        return paymentLogId;
    }

    public void setPaymentLogId(UUID paymentLogId) {
        this.paymentLogId = paymentLogId;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public BigDecimal getVarianceAmount() {
        return varianceAmount;
    }

    public void setVarianceAmount(BigDecimal varianceAmount) {
        this.varianceAmount = varianceAmount;
    }
}
