package ma.nttdata.externals.commons.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard error response object returned to clients.
 */
public record ErrorResponse(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<ValidationError> validationErrors
) {
    /**
     * Default constructor that initializes timestamp to current time.
     */
    public ErrorResponse() {
        this(LocalDateTime.now(), 0, null, null, null, null);
    }

    /**
     * Constructor with HttpStatus.
     */
    public ErrorResponse(HttpStatus status) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), null, null, null);
    }

    /**
     * Constructor with HttpStatus and message.
     */
    public ErrorResponse(HttpStatus status, String message) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, null, null);
    }

    /**
     * Constructor with HttpStatus, message, and path.
     */
    public ErrorResponse(HttpStatus status, String message, String path) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path, null);
    }

    /**
     * Creates a new ErrorResponse with an additional validation error.
     */
    public ErrorResponse addValidationError(String field, String message) {
        List<ValidationError> newErrors = validationErrors == null ? 
            new ArrayList<>() : new ArrayList<>(validationErrors);
        newErrors.add(new ValidationError(field, message));
        return new ErrorResponse(timestamp, status, error, this.message, path, newErrors);
    }

    /**
     * Represents a validation error for a specific field.
     */
    public record ValidationError(String field, String message) {
    }
}
