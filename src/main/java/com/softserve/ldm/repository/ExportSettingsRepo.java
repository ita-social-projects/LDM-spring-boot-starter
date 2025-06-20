package com.softserve.ldm.repository;

import com.softserve.ldm.dto.TableRowsDto;
import com.softserve.ldm.dto.TablesMetadataDto;

public interface ExportSettingsRepo {
    /**
     * Method for receiving metadata about DB table.
     *
     * @return dto {@link TablesMetadataDto}
     */
    TablesMetadataDto getTablesMetadata();

    /**
     * Method for receiving data from table by name, limit and offset.
     *
     * @return dto {@link TableRowsDto}
     */
    TableRowsDto selectPortionFromTable(String tableName, int limit, int offset);

    /**
     * Method for receiving total number of rows in the db table.
     *
     * @param tableName {@link String} DB table name.
     *
     * @return int count of rows.
     */
    int countRowsInTable(String tableName);
}
