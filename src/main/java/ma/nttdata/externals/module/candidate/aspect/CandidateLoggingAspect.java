package ma.nttdata.externals.module.candidate.aspect;

import org.springframework.stereotype.Component;
import ma.nttdata.externals.commons.aspect.LoggingAspect;

/**
 * Candidate-specific logging aspect that extends the common LoggingAspect.
 * This class delegates all logging functionality to the common aspect.
 * It exists to allow for future candidate-specific logging if needed.
 */
@Component
public class CandidateLoggingAspect extends LoggingAspect {
    // All logging functionality is delegated to the parent class
}