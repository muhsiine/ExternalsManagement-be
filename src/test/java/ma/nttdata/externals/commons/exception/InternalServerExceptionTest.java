package ma.nttdata.externals.commons.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class InternalServerExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String message = "Internal error occurred";

        // Act
        InternalServerException exception = new InternalServerException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // Arrange
        String message = "Internal error occurred";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        InternalServerException exception = new InternalServerException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }
}