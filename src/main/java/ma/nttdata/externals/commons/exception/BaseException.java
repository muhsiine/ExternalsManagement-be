package ma.nttdata.externals.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all application-specific exceptions.
 */
@Getter
public abstract class BaseException extends RuntimeException {
    /**
     * -- GETTER --
     *  Returns the HTTP status associated with this exception.
     *
     */
    private final HttpStatus status;

    /**
     * Constructs a new exception with the specified detail message and HTTP status.
     *
     * @param message the detail message
     * @param status the HTTP status to be returned to the client
     */
    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Constructs a new exception with the specified detail message, cause, and HTTP status.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param status the HTTP status to be returned to the client
     */
    public BaseException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

}