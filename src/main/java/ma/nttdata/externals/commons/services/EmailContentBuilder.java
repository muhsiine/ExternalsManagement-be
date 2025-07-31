package ma.nttdata.externals.commons.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Service
public class EmailContentBuilder {

    @Value("${interview.email.template.path}")
    private String InterviewInvitationEmailTemplate;

    public String buildInterviewEmail(String fullName, String offerTitle, String link, String scheduledDate) {
        try{
            String interviewInvitationTemplate = loadTemplate(InterviewInvitationEmailTemplate);
            return  interviewInvitationTemplate.formatted(fullName,offerTitle,scheduledDate,link,link);
        }catch (IOException e){
            throw new RuntimeException("Failed to load email template", e);
        }
    }

    private static String loadTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
