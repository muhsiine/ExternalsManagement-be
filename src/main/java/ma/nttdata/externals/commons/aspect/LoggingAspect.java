package ma.nttdata.externals.commons.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic logging aspect that can be used across all modules.
 * Provides common logging functionality for controller and service methods.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut that matches all controller methods in any module.
     */
    @Pointcut("execution(* ma.nttdata.externals..*.controller.*.*(..))")
    public void controllerMethods() {}

    /**
     * Pointcut that matches all service methods in any module.
     */
    @Pointcut("execution(* ma.nttdata.externals..*.service.impl.*.*(..))")
    public void serviceMethods() {}

    /**
     * Logs method entry for controllers.
     */
    @Before("controllerMethods()")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        log.info("Entering controller method: {}", joinPoint.getSignature().toShortString());
    }

    /**
     * Logs method exit with result for controllers.
     */
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterControllerMethod(JoinPoint joinPoint, Object result) {
        log.info("Exiting controller method: {} with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    /**
     * Logs exceptions thrown from controller methods.
     */
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void logAfterControllerException(JoinPoint joinPoint, Exception exception) {
        log.error("Exception in controller method: {} - Exception: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }

    /**
     * Around advice for service methods to handle logging of method entry, exit, and exceptions.
     */
    @Around("serviceMethods()")
    public Object logAroundServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        // Log method entry
        log.info("Entering service method: {} with arguments: {}", methodName, args);

        try {
            // Execute the method
            Object result = joinPoint.proceed();

            // Log successful completion
            log.info("Service method completed successfully: {} with result: {}", methodName, result);

            return result;
        } catch (Exception e) {
            // Log exception
            log.error("Error in service method: {} - Exception: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }
}
