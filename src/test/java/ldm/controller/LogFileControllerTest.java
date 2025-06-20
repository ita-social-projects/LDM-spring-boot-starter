package ldm.controller;

import com.softserve.ldm.controller.LogFileController;
import com.softserve.ldm.dto.LogFileFilterDto;
import com.softserve.ldm.exception.handler.LdmExceptionHandler;
import com.softserve.ldm.service.DotenvService;
import com.softserve.ldm.service.LogFileService;
import ldm.ModelUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LogFileControllerTest {

    private static final String GET_LOG_FILES_LIST_LINK = "/logs";
    private static final String VIEW_LOG_FILE_LINK = "/logs/view/{filename}";
    private static final String DOWNLOAD_LOG_FILE_LINK = "/logs/download/{filename}";
    private static final String DELETE_DOTENV_FILE_LINK = "/logs/delete-dotenv";
    private static final String SECRET_KEY = "validSecret";

    private MockMvc mockMvc;

    @InjectMocks
    private LogFileController controller;

    @Mock
    private LogFileService logFileService;

    @Mock
    private DotenvService dotenvService;

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new LdmExceptionHandler(errorAttributes))
            .build();
    }

    @Test
    void listLogFilesShouldReturnOkWhenRequestIsValidTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable page = PageRequest.of(pageNumber, pageSize);
        LogFileFilterDto filterDto = ModelUtils.getLogFileFilterDtoWithoutByteSizeRange();
        String requestBody = """
            {
              "filterDto": {
                "fileNameQuery": "filename",
                "fileContentQuery": "fileContent",
                "logLevel": "INFO"
              }
            }
            """;

        mockMvc.perform(post(GET_LOG_FILES_LIST_LINK + "?page=5&size=20")
            .content(requestBody)
            .header("Secret-Key", LogFileControllerTest.SECRET_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(logFileService).listLogFiles(page, filterDto, LogFileControllerTest.SECRET_KEY);
    }

    @Test
    void getLogFileShouldReturnOkWhenRequestIsValidTest() throws Exception {
        String filename = "logfile.log";
        String fileContent = "Log file content";

        when(logFileService.viewLogFileContent(logFileService.sanitizeFilename(filename), LogFileControllerTest.SECRET_KEY))
            .thenReturn(fileContent);

        mockMvc.perform(get(VIEW_LOG_FILE_LINK, filename)
            .contentType(MediaType.TEXT_PLAIN)
            .header("Secret-Key", LogFileControllerTest.SECRET_KEY))
            .andExpect(status().isOk())
            .andExpect(content().string(fileContent));
    }

    @Test
    void downloadLogFileShouldReturnOkWhenFileExistsTest() throws Exception {
        String filename = "logfile.log";
        byte[] fileContent = "Log file content".getBytes();
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        when(logFileService.generateDownloadLogFileUrl(logFileService.sanitizeFilename(filename), LogFileControllerTest.SECRET_KEY))
            .thenReturn(resource);

        mockMvc.perform(get(DOWNLOAD_LOG_FILE_LINK, filename)
            .contentType(MediaType.TEXT_PLAIN)
            .header("Secret-Key", LogFileControllerTest.SECRET_KEY))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + logFileService.sanitizeFilename(filename) + "\""))
            .andExpect(content().bytes(fileContent));
    }

    @Test
    void deleteDotenvFileShouldReturnOkWhenFileIsDeletedTest() throws Exception {

        doNothing().when(dotenvService).deleteDotenvFile(LogFileControllerTest.SECRET_KEY);

        mockMvc.perform(delete(DELETE_DOTENV_FILE_LINK)
            .contentType(MediaType.TEXT_PLAIN)
            .header("Secret-Key", LogFileControllerTest.SECRET_KEY))
            .andExpect(status().isOk());
    }
}
