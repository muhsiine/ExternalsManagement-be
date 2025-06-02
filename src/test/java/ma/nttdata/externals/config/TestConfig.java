package ma.nttdata.externals.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import org.mockito.Mockito;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate aiRestClient() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    @Bean
    @Primary
    public CandidateRepository candidateRepository() {
        return Mockito.mock(CandidateRepository.class);
    }
} 