package com.softserve.ldm.dto;

import jakarta.validation.Valid;
import org.springframework.boot.logging.LogLevel;

public record LogFileFilterDto(
        String fileNameQuery,
        String fileContentQuery,
        @Valid ByteSizeRangeDto byteSizeRangeDto,
        @Valid DateRangeDto dateRangeDto,
        LogLevel logLevel) {
}