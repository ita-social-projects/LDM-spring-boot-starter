package ldm.service;

import com.softserve.ldm.exception.exceptions.BadSecretKeyException;
import com.softserve.ldm.service.impl.DotenvServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DotenvServiceImplTest {

    @InjectMocks
    private DotenvServiceImpl dotenvService;

    @Mock
    private Environment environment;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void validateSecretKeyShouldThrowExceptionWhenKeyDoesNotMatchTest() {
        String expectedSecretKey = "expectedSecretKey";
        String wrongSecretKey = "wrongSecretKey";

        DotenvServiceImpl spyService = spy(dotenvService);

        when(environment.getProperty("LDM_SECRET_KEY")).thenReturn(expectedSecretKey);

        assertThrows(BadSecretKeyException.class, () -> spyService.validateSecretKey(wrongSecretKey));
    }

    @Test
    void validateSecretKeyShouldSucceedWhenKeyMatchesTest() {
        String expectedSecretKey = "expectedSecretKey";
        String passedSecretKey = "expectedSecretKey";

        DotenvServiceImpl spyService = spy(dotenvService);

        when(environment.getProperty("LDM_SECRET_KEY")).thenReturn(expectedSecretKey);

        assertDoesNotThrow(() -> spyService.validateSecretKey(passedSecretKey));
    }
}