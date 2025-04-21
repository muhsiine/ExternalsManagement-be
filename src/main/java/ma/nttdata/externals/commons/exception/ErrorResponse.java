package ma.nttdata.externals.commons.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard error response object returned to clients.
 */
@Setter
@Getter
public class ErrorResponse {
    // Getters and setters
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<ValidationError> validationErrors;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(HttpStatus status) {
        this();
        this.status = status.value();
        this.error = status.getReasonPhrase();
    }

    public ErrorResponse(HttpStatus status, String message) {
        this(status);
        this.message = message;
    }

    public ErrorResponse(HttpStatus status, String message, String path) {
        this(status, message);
        this.path = path;
    }

    public void addValidationError(String field, String message) {
        if (validationErrors == null) {
            validationErrors = new ArrayList<>();
        }
        validationErrors.add(new ValidationError(field, message));
    }

    /**
     * Represents a validation error for a specific field.
     */
    @Setter
    @Getter
    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

    }
}