package com.ledgersync.platform.service.parser.impl;

import com.ledgersync.platform.model.dto.SettlementEntryDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HblExcelParserTest {

    @Test
    void testParseExcel() throws Exception {
        // Given
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Settlements");

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("RRN");
            header.createCell(1).setCellValue("AuthCode");
            header.createCell(2).setCellValue("RequestID");
            header.createCell(3).setCellValue("GrossAmount");
            header.createCell(4).setCellValue("NetAmount");

            // Data
            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue("123456");
            data.createCell(1).setCellValue("AUTH-1");
            data.createCell(2).setCellValue("REQ-1");
            data.createCell(3).setCellValue(100.0);
            data.createCell(4).setCellValue(95.0);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            InputStream is = new ByteArrayInputStream(bos.toByteArray());

            HblExcelParser parser = new HblExcelParser();

            // When
            List<SettlementEntryDto> result = parser.parse(is);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            SettlementEntryDto dto = result.get(0);
            assertEquals("HBL", dto.sourceProvider());
            assertEquals("123456", dto.rrn());
            assertEquals("AUTH-1", dto.authCode());
            assertEquals(new BigDecimal("100.0"), dto.grossAmount());
            assertEquals(new BigDecimal("95.0"), dto.netAmount());
        }
    }
}
