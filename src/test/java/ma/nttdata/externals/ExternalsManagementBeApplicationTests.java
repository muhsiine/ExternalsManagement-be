package ma.nttdata.externals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.docker.compose.enabled=false",
    "app.mock.flag=true"
})
class ExternalsManagementBeApplicationTests {

	@Test
	void contextLoads() {
		// This test will verify that the Spring application context loads successfully
	}

}
