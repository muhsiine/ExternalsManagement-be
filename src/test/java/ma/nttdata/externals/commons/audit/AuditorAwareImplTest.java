package ma.nttdata.externals.commons.audit;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuditorAwareImplTest {

    @Test
    void shouldReturnPersonXAsAuditor() {
        AuditorAwareImpl auditorAware = new AuditorAwareImpl();

        Optional<String> auditor = auditorAware.getCurrentAuditor();

        assertTrue(auditor.isPresent());
        assertEquals("PersonX", auditor.get());
    }
}