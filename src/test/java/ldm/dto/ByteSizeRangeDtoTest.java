package ldm.dto;


import com.ivan.softserve.ldm.dto.ByteSizeRangeDto;
import com.ivan.softserve.ldm.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteSizeRangeDtoTest {

    @Test
    void shouldCreateByteSizeRangeWhenFromSizeIsLessThanOrEqualToToSizeTest() {
        long from = 10L;
        long to = 100L;

        ByteSizeRangeDto byteSizeRangeDto = new ByteSizeRangeDto(from, to);

        assertNotNull(byteSizeRangeDto);
        assertEquals(from, byteSizeRangeDto.from());
        assertEquals(to, byteSizeRangeDto.to());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromSizeIsGreaterThanToSizeTest() {
        long from = 100L;
        long to = 10L;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> new ByteSizeRangeDto(from, to));

        assertEquals("'from' size must be less or equal to 'to' size", exception.getMessage());
    }
}