package ldm.dto;

import com.softserve.ldm.dto.DateRangeDto;
import com.softserve.ldm.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeDtoTest {

    @Test
    void shouldCreateDateRangeWhenFromDateIsBeforeOrEqualToToDateTest() {
        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        DateRangeDto dateRangeDto = new DateRangeDto(from, to);

        assertNotNull(dateRangeDto);
        assertEquals(from, dateRangeDto.from());
        assertEquals(to, dateRangeDto.to());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromDateIsAfterToDateTest() {
        LocalDateTime from = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        LocalDateTime to = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> new DateRangeDto(from, to));

        assertEquals("'from' date must be earlier or equal to 'to' date", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFromDateIsNullTest() {
        LocalDateTime to = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        assertThrows(NullPointerException.class, () -> new DateRangeDto(null, to));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenToDateIsNullTest() {
        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        assertThrows(NullPointerException.class, () -> new DateRangeDto(from, null));
    }
}