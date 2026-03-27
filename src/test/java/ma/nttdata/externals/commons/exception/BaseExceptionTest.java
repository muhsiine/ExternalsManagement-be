package ma.nttdata.externals.commons.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class BaseExceptionTest {

    static class TestException extends BaseException {
        public TestException(String message, HttpStatus status) {
            super(message, status);
        }

        public TestException(String message, Throwable cause, HttpStatus status) {
            super(message, cause, status);
        }
    }

    @Test
    void shouldCreateExceptionWithMessageAndStatus() {
        // Arrange
        String message = "Test error";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Act
        TestException exception = new TestException(message, status);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithMessageCauseAndStatus() {
        // Arrange
        String message = "Test error";
        Throwable cause = new RuntimeException("Root cause");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Act
        TestException exception = new TestException(message, cause, status);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }
}