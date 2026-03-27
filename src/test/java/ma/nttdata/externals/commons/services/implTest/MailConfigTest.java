package ma.nttdata.externals.commons.services.implTest;

import ma.nttdata.externals.commons.services.impl.MailConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MailConfigTest {

    private MailConfig mailConfig;

    @BeforeEach
    void setUp() {
        mailConfig = new MailConfig();

        // Inject @Value fields manually
        ReflectionTestUtils.setField(mailConfig, "host", "smtp.test.com");
        ReflectionTestUtils.setField(mailConfig, "port", 587);
        ReflectionTestUtils.setField(mailConfig, "username", "user@test.com");
        ReflectionTestUtils.setField(mailConfig, "password", "password");

        ReflectionTestUtils.setField(mailConfig, "auth", true);
        ReflectionTestUtils.setField(mailConfig, "starttlsEnable", true);
        ReflectionTestUtils.setField(mailConfig, "connectionTimeout", 5000);
        ReflectionTestUtils.setField(mailConfig, "timeout", 3000);
        ReflectionTestUtils.setField(mailConfig, "writeTimeout", 5000);
    }

    @Test
    void shouldCreateJavaMailSenderWithCorrectConfiguration() {
        // Act
        JavaMailSenderImpl mailSender =
                (JavaMailSenderImpl) mailConfig.javaMailSender();

        // Assert basic config
        assertEquals("smtp.test.com", mailSender.getHost());
        assertEquals(587, mailSender.getPort());
        assertEquals("user@test.com", mailSender.getUsername());
        assertEquals("password", mailSender.getPassword());

        // Assert properties
        Properties props = mailSender.getJavaMailProperties();

        assertEquals("smtp", props.get("mail.transport.protocol"));
        assertEquals(true, props.get("mail.smtp.auth"));
        assertEquals(true, props.get("mail.smtp.starttls.enable"));
        assertEquals(5000, props.get("mail.smtp.connectiontimeout"));
        assertEquals(3000, props.get("mail.smtp.timeout"));
        assertEquals(5000, props.get("mail.smtp.writetimeout"));

        assertEquals("smtp.test.com", props.get("mail.smtp.ssl.trust"));
        assertEquals("false", props.get("mail.debug"));
    }
}