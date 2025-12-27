package com.ledgersync.platform.repository;

import com.ledgersync.platform.model.ReconResult;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ReconResultRepository extends JpaRepository<ReconResult, UUID>, JpaSpecificationExecutor<ReconResult> {
}
