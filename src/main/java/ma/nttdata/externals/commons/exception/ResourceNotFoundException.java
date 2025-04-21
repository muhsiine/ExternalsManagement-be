package ma.nttdata.externals.commons.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BaseException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Constructs a new ResourceNotFoundException with a default message for the specified resource type and identifier.
     *
     * @param resourceType the type of resource that was not found (e.g., "Candidate", "CV")
     * @param id the identifier of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, Object id) {
        super(String.format("%s not found with id: %s", resourceType, id), HttpStatus.NOT_FOUND);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }
}