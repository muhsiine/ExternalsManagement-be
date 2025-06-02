package ma.nttdata.externals.module.interview.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.interview.constants.InterviewPromptConstants;
import ma.nttdata.externals.module.interview.dto.InterviewRequestDTO;
import ma.nttdata.externals.module.interview.service.InterviewSrv;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewSrvImpl implements InterviewSrv {

    private final CandidateRepository candidateRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate aiRestClient;

    public InterviewSrvImpl(CandidateRepository candidateRepository, ObjectMapper objectMapper, RestTemplate aiRestClient) {
        this.candidateRepository = candidateRepository;
        this.objectMapper = configureObjectMapper(objectMapper);
        this.aiRestClient = aiRestClient;
    }

    private ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        return objectMapper
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public String generateInterviewQuestions(InterviewRequestDTO request) {
        log.info("Generating interview questions for candidate ID: {}", request.candidateId());

        // Fetch candidate data
        var candidate = candidateRepository.findById(request.candidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", request.candidateId()));

        try {
            // Create a map for the candidate data
            Map<String, Object> candidateData = new HashMap<>();
            candidateData.put("candidate_id", candidate.getId().toString());
            candidateData.put("full_name", candidate.getFullName());
            candidateData.put("years_of_experience", candidate.getYearsOfExperience());
            candidateData.put("main_tech", candidate.getMainTech());
            candidateData.put("skills", candidate.getSkills());
            candidateData.put("experiences", candidate.getExperiences());
            candidateData.put("educations", candidate.getEducations());
            candidateData.put("languages", candidate.getLanguages());

            // Convert to JSON string with pretty printing for better readability
            String jsonData = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(candidateData);
            
            // Prepare the prompt with the data
            String promptText = InterviewPromptConstants.PROMPT_TEXT
                    .replace("{level}", request.difficultyLevel())
                    .replace("{main_tech}", request.mainTechnology())
                    .replace("{json_data}", jsonData);

            // Send to AI service
            return aiRestClient.postForObject(
                    "/extract",
                    Map.of(
                        "prompt", promptText,
                        "schema", InterviewPromptConstants.JSON_SCHEMA
                    ),
                    String.class
            );
        } catch (Exception e) {
            log.error("Error generating interview questions", e);
            throw new RuntimeException("Failed to generate interview questions", e);
        }
    }
} 