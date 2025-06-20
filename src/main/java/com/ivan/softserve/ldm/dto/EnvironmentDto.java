package com.ivan.softserve.ldm.dto;

import java.util.Map;

public record EnvironmentDto(Map<String, String> variables) {
}