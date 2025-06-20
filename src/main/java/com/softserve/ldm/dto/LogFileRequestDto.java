package com.softserve.ldm.dto;

import jakarta.validation.Valid;

public record LogFileRequestDto(
    @Valid LogFileFilterDto filterDto) {
    public static final String defaultJson = """
        {
            "filterDto": {
              "fileNameQuery": "string",
              "fileContentQuery": "string",
              "byteSizeRangeDto": {
                "from": 0,
                "to": 0
              },
              "dateRangeDto": {
                "from": "2025-01-01T00:00:00",
                "to": "2025-01-01T00:00:00"
              },
              "logLevel": "INFO"
            }
        }
        """;
}
