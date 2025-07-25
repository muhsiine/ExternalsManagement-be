package ma.nttdata.externals.module.interview.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InterviewTokenServImplTest {

    @Autowired
    private InterviewTokenServImpl service;

    @Test
    public void testGenerateToken_returnsNonEmptyString() {

        String token = service.generateToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() >= 43);
    }
}
