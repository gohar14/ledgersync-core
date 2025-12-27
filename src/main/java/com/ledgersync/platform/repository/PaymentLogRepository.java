package com.ledgersync.platform.repository;

import com.ledgersync.platform.model.PaymentLog;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, UUID>, JpaSpecificationExecutor<PaymentLog> {
    Optional<PaymentLog> findByGatewayReference(String gatewayReference);
}
