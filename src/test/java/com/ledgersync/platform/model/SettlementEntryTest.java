package com.ledgersync.platform.model;

import com.ledgersync.platform.model.dto.SettlementEntryDto;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SettlementEntryTest {

    @Test
    void testFlexibleConstructorFeeCalculation() {
        // Given
        SettlementEntryDto dto = new SettlementEntryDto(
                "HBL", "RRN123", "AUTH001", "REQ001",
                new BigDecimal("100.00"), // Gross
                new BigDecimal("95.00"), // Net
                "testItem.xlsx", 1);

        // When
        SettlementEntry entry = new SettlementEntry(dto);

        // Then
        assertNotNull(entry);
        assertEquals(new BigDecimal("5.00"), entry.getFeeAmount());
        assertEquals("PENDING", entry.getReconStatus());
        assertEquals("HBL", entry.getSourceProvider());
    }

    @Test
    void testFlexibleConstructorWithNullGrossAmount() {
        // Given
        SettlementEntryDto dto = new SettlementEntryDto(
                "HBL", "RRN123", "AUTH001", "REQ001",
                null, // Gross is null
                new BigDecimal("95.00"), "test.xlsx", 1);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new SettlementEntry(dto));
    }
}
