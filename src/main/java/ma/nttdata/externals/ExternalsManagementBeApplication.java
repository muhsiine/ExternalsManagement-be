package ma.nttdata.externals;

import org.springframework.ai.model.elevenlabs.autoconfigure.ElevenLabsAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = ElevenLabsAutoConfiguration.class)
public class ExternalsManagementBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExternalsManagementBeApplication.class, args);
	}

}
