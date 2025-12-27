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

import jakarta.persistence.Index;

@Entity
@Table(name = "ls_payment_log", indexes = {
        @Index(name = "idx_payment_gateway_ref", columnList = "gateway_reference")
})
public class PaymentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "internal_id", nullable = false, updatable = false)
    private UUID internalId;

    @Column(name = "gateway_reference", nullable = false)
    private String gatewayReference;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "gateway_provider", nullable = false)
    private String gatewayProvider;

    public PaymentLog() {
    }

    public PaymentLog(UUID internalId, String gatewayReference, BigDecimal amount, String currency,
            LocalDateTime transactionDate, String gatewayProvider) {
        this.internalId = internalId;
        this.gatewayReference = gatewayReference;
        this.amount = amount;
        this.currency = currency;
        this.transactionDate = transactionDate;
        this.gatewayProvider = gatewayProvider;
    }

    public UUID getInternalId() {
        return internalId;
    }

    public void setInternalId(UUID internalId) {
        this.internalId = internalId;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public void setGatewayReference(String gatewayReference) {
        this.gatewayReference = gatewayReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getGatewayProvider() {
        return gatewayProvider;
    }

    public void setGatewayProvider(String gatewayProvider) {
        this.gatewayProvider = gatewayProvider;
    }
}
