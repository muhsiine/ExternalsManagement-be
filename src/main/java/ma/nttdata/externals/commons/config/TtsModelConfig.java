package ma.nttdata.externals.commons.config;

import org.springframework.ai.elevenlabs.api.ElevenLabsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TtsModelConfig {

    @Value("${app.tts.apiKey}")
    private String apiKey;

    @Bean
    public ElevenLabsApi elevenLabsTtsClient() {
        return ElevenLabsApi.builder()
                .apiKey(apiKey)
                .build();
    }
}