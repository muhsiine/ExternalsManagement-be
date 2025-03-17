package ma.nttdata.externals.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean("aiServiceClient")
    public RestClient aiServiceClient(RestClient.Builder builder, @Value("${app.ai.server.url}") String serverUrl) {
        var url = serverUrl + "/ai/api/v1";
        return builder
                .baseUrl(url)
                .defaultHeader("Accept", "application/json")
                .build();
    }

}
