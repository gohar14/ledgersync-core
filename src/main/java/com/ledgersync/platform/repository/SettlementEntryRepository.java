package com.ledgersync.platform.repository;

import com.ledgersync.platform.model.SettlementEntry;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SettlementEntryRepository extends JpaRepository<SettlementEntry, UUID> {
    long countByFileNameAndReconStatus(String fileName, String reconStatus);

    Page<SettlementEntry> findByFileNameAndReconStatus(String fileName, String reconStatus, Pageable pageable);

    long countByReconStatus(String reconStatus);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(e.netAmount) FROM SettlementEntry e WHERE e.reconStatus = :status")
    java.math.BigDecimal sumNetAmountByStatus(@org.springframework.data.repository.query.Param("status") String status);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(e.feeAmount) FROM SettlementEntry e")
    java.math.BigDecimal sumTotalFees();
}
