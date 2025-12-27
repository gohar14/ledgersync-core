package com.ledgersync.platform.service.parser.impl;

import com.ledgersync.platform.model.dto.SettlementEntryDto;
import com.ledgersync.platform.service.parser.BaseInboundParser;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class HblExcelParser implements BaseInboundParser<SettlementEntryDto> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HblExcelParser.class);

    @Override
    public List<SettlementEntryDto> parse(InputStream inputStream) {
        List<SettlementEntryDto> entries = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip header
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                entries.add(mapRowToEntry(row));
            }

        } catch (IOException e) {
            log.error("Error parsing Excel file", e);
            throw new RuntimeException("Failed to parse Excel file", e);
        }
        return entries;
    }

    private SettlementEntryDto mapRowToEntry(Row row) {
        return new SettlementEntryDto(
                "HBL",
                getStringValue(row.getCell(0)),
                getStringValue(row.getCell(1)),
                getStringValue(row.getCell(2)),
                getNumericValue(row.getCell(3)),
                getNumericValue(row.getCell(4)),
                null, // fileName (enriched later)
                row.getRowNum() + 1); // 1-based index
    }

    private String getStringValue(Cell cell) {
        if (cell == null)
            return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private BigDecimal getNumericValue(Cell cell) {
        if (cell == null)
            return BigDecimal.ZERO;

        // Java 25 Pattern Matching for switch (Preview feature in 21, standard in 25
        // hopefully, simulating logic)
        // Note: POI CellType is an enum, so we switch on that.
        // Real Java 25 primitive pattern matching would allow switching on 'double' or
        // 'int' if we were passing raw values,
        // but here we are dealing with POI Cell objects.
        // However, we can use pattern matching for instance checks if we were
        // processing generic objects.
        // For this specific POI use case, the pattern matching is limited to the enum
        // or object type.

        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                try {
                    yield new BigDecimal(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield BigDecimal.ZERO;
                }
            }
            default -> BigDecimal.ZERO;
        };
    }
}
