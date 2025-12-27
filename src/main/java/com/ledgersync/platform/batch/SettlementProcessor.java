package com.ledgersync.platform.batch;

import com.ledgersync.platform.model.SettlementEntry;
import com.ledgersync.platform.model.dto.SettlementEntryDto;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class SettlementProcessor implements ItemProcessor<SettlementEntryDto, SettlementEntry> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SettlementProcessor.class);

    @Override
    public SettlementEntry process(SettlementEntryDto item) throws Exception {
        log.debug("Processing settlement entry: {}", item);
        // The transformation logic including fee calculation is encapsulated in the
        // entity constructor
        // utilizing Java 25 Flexible Constructor Bodies
        return new SettlementEntry(item);
    }
}
