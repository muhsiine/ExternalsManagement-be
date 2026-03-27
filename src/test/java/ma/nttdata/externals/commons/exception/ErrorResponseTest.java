package ma.nttdata.externals.commons.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateDefaultErrorResponse() {
        // Act
        ErrorResponse response = new ErrorResponse();

        // Assert
        assertNotNull(response.timestamp());
        assertEquals(0, response.status());
        assertNull(response.error());
        assertNull(response.message());
        assertNull(response.path());
        assertNull(response.validationErrors());
    }

    @Test
    void shouldCreateErrorResponseWithStatus() {
        // Act
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST);

        // Assert
        assertNotNull(response.timestamp());
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertNull(response.message());
        assertNull(response.path());
    }

    @Test
    void shouldCreateErrorResponseWithStatusAndMessage() {
        // Arrange
        String message = "Invalid input";

        // Act
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, message);

        // Assert
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals(message, response.message());
        assertNull(response.path());
    }

    @Test
    void shouldCreateErrorResponseWithStatusMessageAndPath() {
        // Arrange
        String message = "Invalid input";
        String path = "/api/test";

        // Act
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, message, path);

        // Assert
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals(message, response.message());
        assertEquals(path, response.path());
    }

    @Test
    void shouldAddValidationError() {
        // Arrange
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed");

        // Act
        ErrorResponse updated = response.addValidationError("email", "must not be null");

        // Assert
        assertNotNull(updated.validationErrors());
        assertEquals(1, updated.validationErrors().size());

        ErrorResponse.ValidationError error = updated.validationErrors().get(0);
        assertEquals("email", error.field());
        assertEquals("must not be null", error.message());
    }

    @Test
    void shouldAppendValidationErrors() {
        // Arrange
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed")
                .addValidationError("email", "must not be null");

        // Act
        ErrorResponse updated = response.addValidationError("name", "must not be empty");

        // Assert
        assertEquals(2, updated.validationErrors().size());
    }

    @Test
    void shouldKeepOriginalImmutableWhenAddingValidationError() {
        // Arrange
        ErrorResponse original = new ErrorResponse(HttpStatus.BAD_REQUEST);

        // Act
        ErrorResponse updated = original.addValidationError("field", "error");

        // Assert
        assertNull(original.validationErrors()); // original unchanged
        assertNotNull(updated.validationErrors()); // new instance updated
    }
}