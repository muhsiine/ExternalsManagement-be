package ma.nttdata.externals.module.candidate.aspect;

import ma.nttdata.externals.commons.aspect.LoggingAspect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CandidateLoggingAspectTest {

    @Test
    void shouldCreateInstanceSuccessfully() {
        CandidateLoggingAspect aspect = new CandidateLoggingAspect();

        assertNotNull(aspect);
    }

    @Test
    void shouldBeInstanceOfLoggingAspect() {
        CandidateLoggingAspect aspect = new CandidateLoggingAspect();

        assertTrue(aspect instanceof LoggingAspect);
    }
}