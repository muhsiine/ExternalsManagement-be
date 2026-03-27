package ma.nttdata.externals.commons.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFoundException(ex, request);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Not found", response.getBody().message());
    }


    @Test
    void shouldHandleBadRequestException() {
        BadRequestException ex = new BadRequestException("Bad request");

        ResponseEntity<ErrorResponse> response =
                handler.handleBadRequestException(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Bad request", response.getBody().message());
    }


    @Test
    void shouldHandleInternalServerException() {
        InternalServerException ex = new InternalServerException("Server error");

        ResponseEntity<ErrorResponse> response =
                handler.handleInternalServerException(ex, request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Server error", response.getBody().message());
    }


    @Test
    void shouldHandleValidationException() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "object");

        bindingResult.addError(new FieldError("object", "email", "must not be null"));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response =
                handler.handleValidationExceptions(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(1, response.getBody().validationErrors().size());
        assertEquals("email", response.getBody().validationErrors().get(0).field());
    }


    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(path.toString()).thenReturn("name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");

        ConstraintViolationException ex =
                new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> response =
                handler.handleConstraintViolationException(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(1, response.getBody().validationErrors().size());
    }


    @Test
    void shouldHandleTypeMismatchException() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException(
                        "abc", Integer.class, "id", null, null
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleTypeMismatch(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().message().contains("id"));
    }


    @Test
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected");

        ResponseEntity<ErrorResponse> response =
                handler.handleAllExceptions(ex, request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("An unexpected error occurred", response.getBody().message());
    }
}