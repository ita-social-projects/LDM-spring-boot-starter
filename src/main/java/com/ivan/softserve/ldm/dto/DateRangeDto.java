package com.ivan.softserve.ldm.dto;

import com.ivan.softserve.ldm.exception.exceptions.BadRequestException;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DateRangeDto(
    @NotNull(message = "From date cannot be null") LocalDateTime from,
    @NotNull(message = "To date cannot be null") LocalDateTime to) {
    public DateRangeDto {
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date must be earlier or equal to 'to' date");
        }
    }
}
