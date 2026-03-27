package ma.nttdata.externals.commons.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Signature signature;

    // ===============================
    // Controller Tests
    // ===============================

    @Test
    void shouldLogBeforeControllerMethod() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestController.method()");

        loggingAspect.logBeforeControllerMethod(joinPoint);

        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    @Test
    void shouldLogAfterControllerMethod() {
        Object result = "success";

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestController.method()");

        loggingAspect.logAfterControllerMethod(joinPoint, result);

        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    @Test
    void shouldLogAfterControllerException() {
        Exception exception = new RuntimeException("Test exception");

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestController.method()");

        loggingAspect.logAfterControllerException(joinPoint, exception);

        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    // ===============================
    // Service Tests (Around Advice)
    // ===============================

    @Test
    void shouldReturnResultWhenServiceMethodSucceeds() throws Throwable {
        Object expectedResult = "serviceResult";
        Object[] args = {"arg1", 123};

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestService.method()");
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        when(proceedingJoinPoint.proceed()).thenReturn(expectedResult);

        Object result = loggingAspect.logAroundServiceMethod(proceedingJoinPoint);

        assertEquals(expectedResult, result);
        verify(proceedingJoinPoint).proceed();
    }

    @Test
    void shouldThrowExceptionWhenServiceMethodFails() throws Throwable {
        Object[] args = {"arg1"};

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestService.method()");
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("Service error"));

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> loggingAspect.logAroundServiceMethod(proceedingJoinPoint));

        assertEquals("Service error", thrown.getMessage());
        verify(proceedingJoinPoint).proceed();
    }
}