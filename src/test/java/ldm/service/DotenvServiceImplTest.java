package ldm.service;

import com.softserve.ldm.exception.exceptions.BadSecretKeyException;
import com.softserve.ldm.exception.exceptions.FunctionalityNotAvailableException;
import com.softserve.ldm.service.impl.DotenvServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DotenvServiceImplTest {

    @InjectMocks
    private DotenvServiceImpl dotenvService;

    @Mock
    private Dotenv dotenv;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void validateSecretKeyShouldThrowExceptionWhenKeyDoesNotMatchTest() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).reloadEnvFile();

        when(dotenv.get("logs.secretKey")).thenReturn(secretKey);

        when(passwordEncoder.matches(secretKey, "validSecret")).thenReturn(false);

        assertThrows(BadSecretKeyException.class, () -> spyService.validateSecretKey(secretKey));
    }

    @Test
    void validateSecretKeyShouldSucceedWhenKeyMatchesTest() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).reloadEnvFile();

        when(dotenv.get("logs.secretKey")).thenReturn(secretKey);

        when(passwordEncoder.matches(secretKey, "validSecret")).thenReturn(true);

        spyService.validateSecretKey(secretKey);
    }

    @Test
    void validateSecretKeyShouldThrowFunctionalityNotAvailableExceptionWhenDotenvLoadFailsTest() {
        String secretKey = "secret";

        assertThrows(BadSecretKeyException.class, () -> dotenvService.validateSecretKey(secretKey));
    }

    @Test
    void deleteDotenvFileShouldDeleteIfSecretKeyIsValidTest() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).validateSecretKey(secretKey);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);
            mockedFiles.when(() -> Files.deleteIfExists(any())).thenReturn(true);
            assertDoesNotThrow(() -> spyService.deleteDotenvFile(secretKey));
        }
    }

    @Test
    void deleteDotenvFileShouldThrowFunctionalityNotAvailableExceptionWhenDotenvFileDoesNotExistTest() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).validateSecretKey(secretKey);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(false);
            assertThrows(FunctionalityNotAvailableException.class, () -> spyService.deleteDotenvFile(secretKey));
        }
    }

    @Test
    void deleteDotenvFileShouldThrowFunctionalityNotAvailableExceptionIfCannotDeleteTest() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).validateSecretKey(secretKey);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);
            mockedFiles.when(() -> Files.deleteIfExists(any())).thenReturn(false);
            assertThrows(FunctionalityNotAvailableException.class, () -> spyService.deleteDotenvFile(secretKey));
        }
    }

    @Test
    void deleteDotenvFileShouldThrowFunctionalityNotAvailableExceptionIfDeletingThrowsIOExceptionTest() {
        String secretKey = "validSecret";

        DotenvServiceImpl spyService = spy(dotenvService);

        doNothing().when(spyService).validateSecretKey(secretKey);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);
            mockedFiles.when(() -> Files.deleteIfExists(any())).thenThrow(IOException.class);
            assertThrows(FunctionalityNotAvailableException.class, () -> spyService.deleteDotenvFile(secretKey));
        }
    }
}