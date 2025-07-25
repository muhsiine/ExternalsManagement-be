package ma.nttdata.externals.module.interview.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterviewTokenServImplTest {

    @Test
    public void testGenerateToken_returnsNonEmptyString() {
        InterviewTokenServImpl service = new InterviewTokenServImpl();

        String token = service.generateToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() >= 43);
    }
}
