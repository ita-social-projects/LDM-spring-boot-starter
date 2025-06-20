package ldm;

import com.softserve.ldm.dto.*;
import org.springframework.boot.logging.LogLevel;

import java.util.*;

public class ModelUtils {

    public static TablesMetadataDto getTablesMetadataDto() {
        Map<String, List<String>> tables = new HashMap<>();
        List<String> columns = List.of("id", "name", "email");
        tables.put("users", columns);

        return new TablesMetadataDto(tables);
    }

    public static TableRowsDto getTableRowsDto() {
        List<Map<String, String>> tableData = new LinkedList<>();
        Map<String, String> row = new LinkedHashMap<>();
        row.put("id", "1");
        row.put("date_of_registration", "1970-01-01 00:00:00");
        row.put("email", "someemail@some.com");
        row.put("name", "Name");
        row.put("role", "ROLE_ADMIN");
        tableData.add(row);

        return new TableRowsDto("users", tableData);
    }

    public static TableParamsRequestDto tableParamsRequestDto() {
        return new TableParamsRequestDto("users", 10, 1);
    }

    public static EnvironmentDto getEnvironmentDto() {
        Map<String, String> env = new HashMap<>();
        env.put("TEST_ENV_NAME", "TEST_ENV_VALUE");
        return new EnvironmentDto(env);
    }

    public static PageableAdvancedDto<Map<String, String>> getPageableAdvancedDtoForTableRows() {
        return new PageableAdvancedDto<>(
                getTableRowsDto().tableData(),
                1,
                0,
                1,
                0,
                false,
                true,
                true,
                true);
    }

    public static LogFileFilterDto getLogFileFilterDto() {
        return new LogFileFilterDto("test",
                null,
                new ByteSizeRangeDto(0, 1000),
                null,
                null);
    }

    public static LogFileFilterDto getLogFileFilterDtoWithoutByteSizeRange() {
        return new LogFileFilterDto(
                "filename",
                "fileContent",
                null,
                null,
                LogLevel.INFO);
    }
}
