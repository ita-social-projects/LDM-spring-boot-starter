package com.softserve.ldm.controller;

import com.softserve.ldm.constant.AppConstant;
import com.softserve.ldm.constant.ErrorMessage;
import com.softserve.ldm.constant.HttpStatuses;
import com.softserve.ldm.dto.PageableAdvancedDto;
import com.softserve.ldm.dto.EnvironmentDto;
import com.softserve.ldm.dto.TableParamsRequestDto;
import com.softserve.ldm.dto.TableRowsDto;
import com.softserve.ldm.dto.TablesMetadataDto;
import com.softserve.ldm.service.ExportSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that provides endpoints for accessing database table metadata,
 * retrieving data, exporting to Excel, and fetching environment variables.
 * <p>
 * All endpoints require a valid secret key passed via the {@code Secret-Key} header.
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/export/settings")
public class ExportSettingsController {
    private final ExportSettingsService exportSettingsService;

    /**
     * Retrieves the names of all database tables and their columns.
     *
     * @param secretKey Secret key for authentication
     * @return {@link TablesMetadataDto} containing metadata about available tables
     */
    @Operation(summary = "Get all tables names and columns.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = TablesMetadataDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
    })
    @GetMapping(value = "/tables", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TablesMetadataDto> getTablesInfo(
            @RequestHeader(name = "Secret-Key") String secretKey
    ) {
        return ResponseEntity.ok(exportSettingsService.getTablesMetadata(secretKey));
    }

    /**
     * Retrieves rows from the specified database table using pagination.
     *
     * @param tableName Name of the table (must match validation pattern)
     * @param pageable  Pagination parameters (limit, offset, etc.)
     * @param secretKey Secret key for authentication
     * @return {@link PageableAdvancedDto} containing a list of rows as key-value pairs
     */
    @Operation(summary = "Get table rows by params.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = TableRowsDto.class))),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/select")
    public ResponseEntity<PageableAdvancedDto<Map<String, String>>> selectFromTable(
        @Pattern(regexp = AppConstant.VALID_TABLE_NAME_REGEX,
            message = ErrorMessage.INVALID_TABLE_NAME) String tableName,
        @Parameter(hidden = true) Pageable pageable,
        @RequestHeader(name = "Secret-Key") String secretKey
    ) {
        return ResponseEntity.ok(exportSettingsService.selectFromTable(tableName, pageable, secretKey));
    }

    /**
     * Exports data from a specific table to an Excel (.xlsx) file.
     *
     * @param tableParams Table parameters including name, offset, and limit
     * @param secretKey   Secret key for authentication
     * @return {@link InputStreamResource} representing the downloadable Excel file
     */
    @Operation(summary = "Get excel file with table rows by params.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
    })
    @GetMapping("/download-table-data")
    public ResponseEntity<InputStreamResource> exportTableRowsAsExcel(
            @Valid TableParamsRequestDto tableParams,
            @RequestHeader(name = "Secret-Key") String secretKey
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            String.format("attachment; filename= %s(%d - %d).xlsx", tableParams.tableName(), tableParams.offset(),
                tableParams.limit()));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
            .headers(headers)
            .body(new InputStreamResource(
                exportSettingsService.getExcelFileAsResource(tableParams, secretKey)));
    }

    /**
     * Retrieves all environment variables currently used by the application.
     *
     * @param secretKey Secret key for authentication
     * @return {@link EnvironmentDto} containing environment variables and their values
     */
    @Operation(summary = "Get all environment variables")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = EnvironmentDto.class))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
    })
    @GetMapping("/env")
    public ResponseEntity<EnvironmentDto> getEnvVariables(@RequestHeader(name = "Secret-Key") String secretKey) {
        return ResponseEntity.ok(exportSettingsService.getEnvironmentVariables(secretKey));
    }
}
