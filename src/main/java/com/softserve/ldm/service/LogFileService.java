package com.softserve.ldm.service;

import com.softserve.ldm.dto.PageableDto;
import com.softserve.ldm.dto.LogFileMetadataDto;
import com.softserve.ldm.dto.LogFileFilterDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for working with application log files.
 * <p>
 * Provides functionality to list, read, download, and sanitize log file names.
 * </p>
 */
public interface LogFileService {

    /**
     * Retrieves a paginated list of log files with metadata such as filename, size, and last modified date.
     *
     * @param page       {@link Pageable} pagination information.
     * @param filterDto  {@link LogFileFilterDto} optional filters to apply (e.g. by filename, size, level).
     * @param secretKey  Secret key for authentication.
     * @return {@link PageableDto} containing metadata for each matching log file.
     */
    PageableDto<LogFileMetadataDto> listLogFiles(Pageable page, LogFileFilterDto filterDto, String secretKey);

    /**
     * Reads and returns the content of a specified log file as a plain text string.
     *
     * @param filename   Name of the log file to be read.
     * @param secretKey  Secret key for authentication.
     * @return Log file content as a {@link String}.
     */
    String viewLogFileContent(String filename, String secretKey);

    /**
     * Generates a {@link Resource} for downloading a specified log file.
     *
     * @param filename   Name of the log file to be downloaded.
     * @param secretKey  Secret key for authentication.
     * @return {@link Resource} representing the downloadable log file.
     */
    Resource generateDownloadLogFileUrl(String filename, String secretKey);

    /**
     * Sanitizes a filename by replacing all invalid characters with underscores.
     * <p>
     * Allowed characters: letters (a-z, A-Z), digits (0-9), dot (.), underscore (_), and hyphen (-).
     * All other characters are replaced with an underscore to ensure filesystem safety.
     * </p>
     *
     * @param filename Original filename.
     * @return Sanitized filename safe for use in file paths.
     */
    String sanitizeFilename(String filename);
}
