package com.ivan.softserve.ldm.service;

import com.ivan.softserve.ldm.dto.TableRowsDto;

import java.io.InputStream;

public interface ExportToFileService {
    /**
     * Exports the given table data to an Excel (.xlsx) file and returns it as an input stream.
     *
     * @param data {@link TableRowsDto} containing the table rows to be exported.
     * @return {@link InputStream} representing the generated Excel file.
     */
    InputStream exportTableDataToExcel(TableRowsDto data);
}
