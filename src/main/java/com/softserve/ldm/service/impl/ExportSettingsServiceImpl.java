package com.softserve.ldm.service.impl;

import com.softserve.ldm.dto.PageableAdvancedDto;
import com.softserve.ldm.dto.EnvironmentDto;
import com.softserve.ldm.dto.TableParamsRequestDto;
import com.softserve.ldm.dto.TableRowsDto;
import com.softserve.ldm.dto.TablesMetadataDto;
import com.softserve.ldm.repository.ExportSettingsRepo;
import com.softserve.ldm.service.DotenvService;
import com.softserve.ldm.service.ExportSettingsService;
import com.softserve.ldm.service.ExportToFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ExportSettingsServiceImpl implements ExportSettingsService {
    private final ExportSettingsRepo exportSettingsRepo;
    private final ExportToFileService exportToFileService;
    private final DotenvService dotenvService;

    /**
     * {@inheritDoc}
     */
    @Override
    public TablesMetadataDto getTablesMetadata(String secretKey) {

        dotenvService.validateSecretKey(secretKey);

        return exportSettingsRepo.getTablesMetadata();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageableAdvancedDto<Map<String, String>> selectFromTable(String tableName, Pageable pageable, String secretKey) {

        dotenvService.validateSecretKey(secretKey);

        int totalElements = exportSettingsRepo.countRowsInTable(tableName);
        TableRowsDto data = exportSettingsRepo.selectPortionFromTable(
            tableName, pageable.getPageSize(), (int) pageable.getOffset());

        return populatePageableDto(totalElements, pageable, data.tableData());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public InputStream getExcelFileAsResource(TableParamsRequestDto tableParams, String secretKey) {

        dotenvService.validateSecretKey(secretKey);

        TableRowsDto data = exportSettingsRepo.selectPortionFromTable(tableParams.tableName(), tableParams.limit(),
            tableParams.offset());

        return exportToFileService.exportTableDataToExcel(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnvironmentDto getEnvironmentVariables(String secretKey) {

        dotenvService.validateSecretKey(secretKey);

        return new EnvironmentDto(System.getenv());
    }

    /**
     * Populates a {@link PageableAdvancedDto} object with paginated data and metadata.
     *
     * @param totalElements Total number of records in the dataset.
     * @param pageable       {@link Pageable} object containing pagination information such as page number and size.
     * @param data           List of records for the current page, represented as key-value pairs.
     * @return {@link PageableAdvancedDto} containing the current page data and pagination metadata.
     */
    private PageableAdvancedDto<Map<String, String>> populatePageableDto(int totalElements, Pageable pageable,
        List<Map<String, String>> data) {
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        boolean isFirst = pageable.getPageNumber() == 0;
        boolean isLast = pageable.getPageNumber() + 1 >= totalPages;

        return new PageableAdvancedDto<>(
            data,
            totalElements,
            pageable.getPageNumber(),
            totalPages,
            pageable.getPageNumber(),
            pageable.getPageNumber() > 0,
            !isLast,
            isFirst,
            isLast);
    }
}
