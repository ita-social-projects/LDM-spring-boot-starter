package com.ivan.softserve.ldm.service.impl;


import com.ivan.softserve.ldm.constant.ErrorMessage;
import com.ivan.softserve.ldm.dto.TableRowsDto;
import com.ivan.softserve.ldm.exception.exceptions.FileGenerationException;
import com.ivan.softserve.ldm.exception.exceptions.ResourceNotFoundException;
import com.ivan.softserve.ldm.service.ExportToFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ExportToFileServiceImpl implements ExportToFileService {
    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public InputStream exportTableDataToExcel(TableRowsDto data) {
        if (data.tableData().isEmpty()) {
            throw new ResourceNotFoundException(String.format(ErrorMessage.EMPTY_TABLE, data.tableData()));
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(data.tableName());
        createHeaderRow(workbook, sheet, data);
        populateTableCells(workbook, sheet, data);
        return convertWorkbookToInputStream(workbook);
    }

    /**
     * Creates the header row in the provided Excel sheet using column names
     * from the first row of table data. Applies a bold style with background color.
     *
     * @param workbook The Excel workbook being generated.
     * @param sheet    The sheet where the header will be created.
     * @param data     {@link TableRowsDto} containing table data to extract column headers from.
     */
    private void createHeaderRow(Workbook workbook, Sheet sheet, TableRowsDto data) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);

        Set<String> raw = data.tableData().getFirst().keySet();
        int cellIndex = 0;
        for (String key : raw) {
            Cell headerCell = header.createCell(cellIndex++);
            headerCell.setCellValue(key);
            headerCell.setCellStyle(headerStyle);
        }
    }

    /**
     * Populates the Excel sheet with table rows starting from the second row (index 1).
     * Matches each value to the corresponding column name in the header row.
     *
     * @param workbook The Excel workbook being populated.
     * @param sheet    The sheet where data will be inserted.
     * @param data     {@link TableRowsDto} containing the table data to write into the sheet.
     */
    private void populateTableCells(Workbook workbook, Sheet sheet, TableRowsDto data) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        Row headerRow = sheet.getRow(0);
        List<Map<String, String>> tableData = data.tableData();

        int rowIndex = 1;
        for (Map<String, String> r : tableData) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;

            for (Map.Entry<String, String> entry : r.entrySet()) {
                Cell headerCell = headerRow.getCell(cellIndex);
                if (headerCell != null && headerCell.getStringCellValue().equals(entry.getKey())) {
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(entry.getValue());
                    cell.setCellStyle(style);
                }
                cellIndex++;
            }
        }
    }

    /**
     * Converts the provided {@link Workbook} into an {@link InputStream}, which can be used
     * for file download or further processing.
     *
     * @param workbook The Excel workbook to convert.
     * @return {@link InputStream} containing the binary representation of the Excel file.
     * @throws FileGenerationException if an I/O error occurs during conversion.
     */
    public InputStream convertWorkbookToInputStream(Workbook workbook) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            workbook.close();

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileGenerationException(ErrorMessage.GENERATION_EXCEL_FILE_ERROR, e);
        }
    }
}
