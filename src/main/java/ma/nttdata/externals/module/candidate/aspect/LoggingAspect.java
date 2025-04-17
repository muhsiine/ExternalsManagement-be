package ma.nttdata.externals.module.candidate.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* ma.nttdata.externals.module.candidate.controller.*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Entering method: {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "execution(* ma.nttdata.externals.module.candidate.controller.*.*(..))", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        logger.info("Exiting method: {} with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(pointcut = "execution(* ma.nttdata.externals.module.candidate.controller.*.*(..))", throwing = "exception")
    public void logAfterException(JoinPoint joinPoint, Exception exception) {
        logger.error("Exception in method: {} - Exception: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }
}
