package ma.nttdata.externals.commons.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an unexpected error occurs during processing.
 */
public class InternalServerException extends BaseException {

    /**
     * Constructs a new InternalServerException with the specified detail message.
     *
     * @param message the detail message
     */
    public InternalServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Constructs a new InternalServerException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InternalServerException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}