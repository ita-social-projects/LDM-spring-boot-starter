package com.softserve.ldm.dto;

import java.util.List;
import java.util.Map;

public record TableRowsDto(String tableName, List<Map<String, String>> tableData) {
}
