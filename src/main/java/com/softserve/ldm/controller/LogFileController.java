package com.softserve.ldm.controller;

import com.softserve.ldm.annotations.ApiPageable;
import com.softserve.ldm.constant.HttpStatuses;
import com.softserve.ldm.dto.PageableDto;
import com.softserve.ldm.dto.LogFileMetadataDto;
import com.softserve.ldm.dto.LogFileRequestDto;
import com.softserve.ldm.service.DotenvService;
import com.softserve.ldm.service.LogFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for managing application log files.
 * <p>
 * Provides endpoints to:
 * <ul>
 *     <li>Retrieve a paginated list of log file metadata</li>
 *     <li>View the content of a specific log file</li>
 *     <li>Download a specific log file</li>
 *     <li>Delete the <code>.env</code> file used for configuration</li>
 * </ul>
 * <p>
 * All endpoints require the client to provide a valid secret key via the <code>Secret-Key</code> header.
 */

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/logs")
public class LogFileController {
    private final LogFileService logFileService;
    private final DotenvService dotenvService;

    /**
     * Retrieves a paginated list of log file metadata based on optional filters.
     *
     * @param requestDto DTO containing optional filter parameters
     * @param secretKey  Secret key required for authorization
     * @param page       Pagination information
     * @return Paginated list of log file metadata
     */
    @Operation(summary = "Returns a list of log files metadata from project directory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(example = LogFileMetadataDto.defaultJson))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
                    content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @ApiPageable
    @PostMapping
    public ResponseEntity<PageableDto<LogFileMetadataDto>> listLogFiles(
            @Schema(
                    description = "Filters for logs",
                    name = "LogFileFilterDto",
                    type = "object",
                    example = LogFileRequestDto.defaultJson) @RequestBody @NotNull @Valid LogFileRequestDto requestDto,
            @RequestHeader(name = "Secret-Key") String secretKey,
            @Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(logFileService.listLogFiles(page, requestDto.filterDto(), secretKey));
    }

    /**
     * Returns the content of a log file by its filename.
     *
     * @param secretKey Secret key required for authorization
     * @param filename  Name of the log file to view
     * @return File content as plain text
     */
    @Operation(summary = "Returns content of a file with given filename")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(example = "string"))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
                    content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
            @ApiResponse(responseCode = "503", description = HttpStatuses.SERVICE_UNAVAILABLE,
                    content = @Content(examples = @ExampleObject(HttpStatuses.SERVICE_UNAVAILABLE)))
    })
    @GetMapping("/view/{filename}")
    public ResponseEntity<String> viewLogFileContent(
            @RequestHeader(name = "Secret-Key") String secretKey,
            @PathVariable String filename) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(logFileService.viewLogFileContent(logFileService.sanitizeFilename(filename), secretKey));
    }

    /**
     * Provides a downloadable resource representing the specified log file.
     *
     * @param secretKey Secret key required for authorization
     * @param filename  Name of the log file to download
     * @return File resource with appropriate headers to trigger browser download
     */
    @Operation(summary = "Returns a url that triggers file download in a browser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(example = HttpStatuses.OK))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
                    content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadLogFile(
            @RequestHeader(name = "Secret-Key") String secretKey,
            @PathVariable String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + logFileService.sanitizeFilename(filename) + "\"")
                .body(logFileService.generateDownloadLogFileUrl(logFileService.sanitizeFilename(filename), secretKey));
    }
}