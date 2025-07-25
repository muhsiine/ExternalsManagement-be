package ma.nttdata.externals.commons.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.InternalServerException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;



    public void sendEmail(String to, String subject, String dynamicHtmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("tutorial.genuinecoder@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(dynamicHtmlBody, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new InternalServerException("Failed to send email", e);
        }
    }
}
