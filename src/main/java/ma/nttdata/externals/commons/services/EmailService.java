package ma.nttdata.externals.commons.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.InternalServerException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String dynamicHtmlBody) {
        if (mailSender == null) {
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("tt6677798@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(dynamicHtmlBody, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new InternalServerException("Failed to send email", e);
        }
    }
}
