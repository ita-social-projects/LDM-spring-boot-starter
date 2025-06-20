package ldm.service;

import com.softserve.ldm.dto.PageableAdvancedDto;
import com.softserve.ldm.dto.EnvironmentDto;
import com.softserve.ldm.dto.TableParamsRequestDto;
import com.softserve.ldm.dto.TableRowsDto;
import com.softserve.ldm.dto.TablesMetadataDto;
import com.softserve.ldm.repository.ExportSettingsRepo;
import com.softserve.ldm.service.DotenvService;
import com.softserve.ldm.service.impl.ExportSettingsServiceImpl;
import com.softserve.ldm.service.ExportToFileService;
import ldm.ModelUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportSettingsServiceImplTest {
    private static final String TABLE_NAME = "users";
    private static final Pageable pageable = PageRequest.of(0, 10);
    private final TableParamsRequestDto tableParams = ModelUtils.tableParamsRequestDto();

    @InjectMocks
    private ExportSettingsServiceImpl settingsService;

    @Mock
    private ExportSettingsRepo exportSettingsRepo;

    @Mock
    private ExportToFileService exportToFileService;

    @Mock
    private DotenvService dotenvService;

    @Test
    void getTablesMetadataTest() {

        String secretKey = "validSecret";

        TablesMetadataDto tablesMetadataDto = ModelUtils.getTablesMetadataDto();
        when(exportSettingsRepo.getTablesMetadata()).thenReturn(tablesMetadataDto);

        TablesMetadataDto result = settingsService.getTablesMetadata(secretKey);

        assertNotNull(result);
        verify(exportSettingsRepo, times(1)).getTablesMetadata();
    }

    @Test
    void selectFromTableWithValidParamsTest() {

        String secretKey = "validSecret";

        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, pageable.getPageSize(), (int) pageable.getOffset()))
            .thenReturn(tableRowsDto);

        PageableAdvancedDto<Map<String, String>> result = settingsService.selectFromTable(TABLE_NAME, pageable, secretKey);

        assertFalse(result.getPage().isEmpty());
        verify(exportSettingsRepo, times(1)).selectPortionFromTable(TABLE_NAME, pageable.getPageSize(),
            (int) pageable.getOffset());
    }

    @Test
    void selectFromTableWithValidParamsAndResultMoreThenOnePageTest() {

        String secretKey = "validSecret";

        TableRowsDto tableRowsDto = populateTableRowDto();
        Pageable pageableForSecondPage = PageRequest.of(2, 2);
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, pageableForSecondPage.getPageSize(),
            (int) pageableForSecondPage.getOffset()))
            .thenReturn(tableRowsDto);
        when(exportSettingsRepo.countRowsInTable(TABLE_NAME)).thenReturn(tableRowsDto.tableData().size());

        PageableAdvancedDto<Map<String, String>> result =
            settingsService.selectFromTable(TABLE_NAME, pageableForSecondPage, secretKey);

        assertEquals(result.getTotalElements(), tableRowsDto.tableData().size());
        verify(exportSettingsRepo, times(1)).selectPortionFromTable(TABLE_NAME, pageableForSecondPage.getPageSize(),
            (int) pageableForSecondPage.getOffset());
    }

    @Test
    void getExcelFileAsResourceWithValidParamsTest() {

        String secretKey = "validSecret";

        InputStream excelResource = new ByteArrayInputStream(new byte[] {1, 2, 3, 4, 5});
        TableRowsDto tableRowsDto = ModelUtils.getTableRowsDto();
        when(exportSettingsRepo.selectPortionFromTable(TABLE_NAME, tableParams.limit(), tableParams.offset()))
            .thenReturn(tableRowsDto);
        when(exportToFileService.exportTableDataToExcel(tableRowsDto)).thenReturn(excelResource);

        InputStream result = settingsService.getExcelFileAsResource(tableParams, secretKey);

        assertNotNull(result);
        verify(exportToFileService, times(1)).exportTableDataToExcel(tableRowsDto);
    }

    @Test
    void getEnvironmentVariablesTest() {

        String secretKey = "validSecret";

        EnvironmentDto result = settingsService.getEnvironmentVariables(secretKey);

        assertFalse(result.variables().isEmpty());
    }

    private TableRowsDto populateTableRowDto() {
        List<Map<String, String>> tableData = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("id", String.valueOf(i));
            row.put("date_of_registration", "1970-01-01 00:00:00");
            row.put("email", "someemail" + i + "@some.com");
            row.put("name", "Name" + i);
            row.put("role", "ROLE_USER");
            tableData.add(row);
        }
        return new TableRowsDto("users", tableData);
    }
}
