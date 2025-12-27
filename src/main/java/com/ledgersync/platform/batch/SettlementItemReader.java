package com.ledgersync.platform.batch;

import com.ledgersync.platform.model.dto.SettlementEntryDto;
import com.ledgersync.platform.service.parser.BaseInboundParser;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.Resource;

public class SettlementItemReader implements ItemReader<SettlementEntryDto> {

    private final BaseInboundParser<SettlementEntryDto> parser;
    private final Resource resource;
    private Iterator<SettlementEntryDto> iterator;

    public SettlementItemReader(BaseInboundParser<SettlementEntryDto> parser, Resource resource) {
        this.parser = parser;
        this.resource = resource;
    }

    @Override
    public SettlementEntryDto read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (iterator == null) {
            List<SettlementEntryDto> entries = parser.parse(resource.getInputStream());
            iterator = entries.iterator();
        }

        if (iterator.hasNext()) {
            SettlementEntryDto original = iterator.next();
            // Enrich with fileName
            return new SettlementEntryDto(
                    original.sourceProvider(),
                    original.rrn(),
                    original.authCode(),
                    original.requestId(),
                    original.grossAmount(),
                    original.netAmount(),
                    resource.getFilename(),
                    original.rowNumber());
        }
        return null;
    }
}
