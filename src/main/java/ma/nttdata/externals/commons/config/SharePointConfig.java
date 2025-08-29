package ma.nttdata.externals.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.sharepoint")
public class SharePointConfig {

    private String tenantId;
    private String clientId;
    private String clientSecret;
    private String siteUrl;
    private String documentLibrary;
}
