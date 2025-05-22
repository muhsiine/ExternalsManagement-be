package ma.nttdata.externals.commons.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the client sends a bad request.
 * This includes validation errors, malformed requests, etc.
 */
public class BadRequestException extends BaseException {

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message
     */
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Constructs a new BadRequestException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}