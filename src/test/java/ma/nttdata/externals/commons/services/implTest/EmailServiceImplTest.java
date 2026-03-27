package ma.nttdata.externals.commons.services.implTest;


import jakarta.mail.internet.MimeMessage;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.commons.services.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject sender manually (because of @Value)
        org.springframework.test.util.ReflectionTestUtils
                .setField(emailService, "sender", "test@nttdata.com");
    }

    @Test
    void shouldSendEmailSuccessfully() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendEmail("to@test.com", "Subject", "<h1>Hello</h1>");

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void shouldThrowInternalServerExceptionWhenMessagingFails() {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("fail"))
                .when(mailSender).send(mimeMessage);

        InternalServerException exception = assertThrows(
                InternalServerException.class,
                () -> emailService.sendEmail("to", "subject", "body")
        );

        assertEquals("Failed to send email", exception.getMessage());
    }


    @Test
    void shouldDoNothingWhenMailSenderIsNull() {
        EmailServiceImpl service = new EmailServiceImpl(null);

        service.sendEmail("to@test.com", "Subject", "Body");

        // no exception → success
        assertTrue(true);
    }
}