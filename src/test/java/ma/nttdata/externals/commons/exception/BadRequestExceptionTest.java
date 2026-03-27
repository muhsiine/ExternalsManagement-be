package ma.nttdata.externals.commons.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String message = "Invalid request";

        // Act
        BadRequestException exception = new BadRequestException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // Arrange
        String message = "Invalid request";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        BadRequestException exception = new BadRequestException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }
}