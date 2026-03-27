package ma.nttdata.externals.commons.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String message = "Resource not found";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithResourceTypeAndId() {
        // Arrange
        String resourceType = "Candidate";
        Long id = 123L;

        // Act
        ResourceNotFoundException exception =
                new ResourceNotFoundException(resourceType, id);

        // Assert
        assertEquals("Candidate not found with id: 123", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // Arrange
        String message = "Error occurred";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        ResourceNotFoundException exception =
                new ResourceNotFoundException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }
}