package com.ledgersync.platform.service;

import java.util.UUID;

public interface ReconciliationService {
    void performMatch(UUID settlementId);
}
