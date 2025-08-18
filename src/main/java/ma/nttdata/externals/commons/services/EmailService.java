package ma.nttdata.externals.commons.services;

public interface EmailService {

    void sendEmail(String to, String subject, String dynamicHtmlBody);
}
