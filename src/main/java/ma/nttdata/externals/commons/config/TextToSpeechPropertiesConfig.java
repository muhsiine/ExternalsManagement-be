package ma.nttdata.externals.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.tts")
public class TextToSpeechPropertiesConfig {

    private String apiKey;
    private String modelId;
    private String voiceId;
    private Double stability;
    private Double similarityBoost;
    private Double style;
    private boolean useSpeakerBoost;
    private Double speed;
}
