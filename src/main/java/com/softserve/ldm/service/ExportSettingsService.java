package com.softserve.ldm.service;

import com.softserve.ldm.dto.PageableAdvancedDto;
import com.softserve.ldm.dto.EnvironmentDto;
import com.softserve.ldm.dto.TableParamsRequestDto;
import com.softserve.ldm.dto.TablesMetadataDto;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.Map;

public interface ExportSettingsService {

    /**
     * Retrieves metadata for all database tables, including their names and column information.
     *
     * @param secretKey Secret key for authentication.
     * @return {@link TablesMetadataDto} containing metadata about all available tables.
     */
    TablesMetadataDto getTablesMetadata(String secretKey);

    /**
     * Retrieves paginated rows from the specified database table.
     *
     * @param tableName Name of the database table.
     * @param pageable  {@link Pageable} object containing pagination and sorting information.
     * @param secretKey Secret key for authentication.
     * @return {@link PageableAdvancedDto} containing table rows as key-value pairs.
     */
    PageableAdvancedDto<Map<String, String>> selectFromTable(String tableName, Pageable pageable, String secretKey);

    /**
     * Generates an Excel (.xlsx) file containing rows from the specified table using the provided parameters.
     *
     * @param tableParams {@link TableParamsRequestDto} containing table name, offset, and limit.
     * @param secretKey   Secret key for authentication.
     * @return {@link InputStream} representing the generated Excel file as a stream.
     */
    InputStream getExcelFileAsResource(TableParamsRequestDto tableParams, String secretKey);

    /**
     * Retrieves all environment variables currently used by the application.
     *
     * @param secretKey Secret key for authentication.
     * @return {@link EnvironmentDto} containing environment variable names and their values.
     */
    EnvironmentDto getEnvironmentVariables(String secretKey);
}
