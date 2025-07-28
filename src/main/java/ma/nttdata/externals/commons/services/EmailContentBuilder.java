package ma.nttdata.externals.commons.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class EmailContentBuilder {

    public static String buildInterviewEmail(String fullName, String offerTitle, String link, String scheduledDate) {
        try{
            String interviewInvitationTemplate = loadTemplate("templates/interviewsInvitation.html");
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
