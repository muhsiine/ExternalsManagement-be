package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.commons.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InterviewTokenServImplTest {

    @Autowired
    private InterviewTokenServImpl service;

    @MockitoBean
    private EmailService emailService;

    @Test
    public void testGenerateToken_returnsNonEmptyString() {

        String token = service.generateToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() >= 43);
    }
}
