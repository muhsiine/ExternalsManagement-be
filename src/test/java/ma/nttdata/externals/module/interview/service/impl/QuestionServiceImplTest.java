package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.InterviewQuestionsPromptPlaceholdersDTO;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.QuestionResponseFromAI;
import ma.nttdata.externals.module.interview.mapper.QuestionMapper;
import ma.nttdata.externals.module.interview.repository.InterviewRepository;
import ma.nttdata.externals.module.interview.repository.QuestionRepository;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private RestClient aiRestClient;

    private QuestionServImpl questionServ;

    private boolean mockFlag = true;



    @Test
    void PrepareQuestionsFromAIResponse_shouldParseJsonCorrectly() {

        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                true,
                aiRestClient);
        CandidateDTO candidateDTO = new CandidateDTO(UUID.randomUUID(), null, null, 0, null, null, null, null, null, null, null, null, null, null, null);
        OfferDTO offerDTO = new OfferDTO(UUID.randomUUID(), null, null, null);

        InterviewQuestionsPromptPlaceholdersDTO generateQuestionsInfo = new InterviewQuestionsPromptPlaceholdersDTO(
                candidateDTO,
                offerDTO,
                3,
                30
        );

        List<EvaluationTypeDTO> evaluationTypes = List.of(
                new EvaluationTypeDTO(UUID.randomUUID(), "Technical", 1.0)
        );


        List<QuestionDTO> questions = questionServ.prepareQuestionsFromAIResponse(generateQuestionsInfo, evaluationTypes);


        assertNotNull(questions);
        assertEquals(15, questions.size());
        assertTrue(questions.stream().allMatch(q -> q.description() != null));
    }

    @Test
    void PrepareQuestionsFromAIResponse_WithMockFlagFalse_shouldParseJsonCorrectly() {
        questionServ = new QuestionServImpl(
                questionRepository,
                questionMapper,
                interviewRepository,
                false,
                aiRestClient);
        List<QuestionResponseFromAI> aiResponses = List.of(
                new QuestionResponseFromAI("What is Java?", "3"),
                new QuestionResponseFromAI("Explain REST APIs.", "4"),
                new QuestionResponseFromAI("Describe microservices.", "5"),
                new QuestionResponseFromAI("What is Spring Boot?", "6")
        );

        List<QuestionDTO> questions = aiResponses.stream()
                .map(raw -> new QuestionDTO(
                        null,
                        raw.description(),
                        parseDuration(raw.durationInMinutes()),
                        null,
                        null
                ))
                .toList();

        assertEquals(4, questions.size());

        assertEquals(3, questions.get(0).durationInMinutes());
        assertEquals(4, questions.get(1).durationInMinutes());
        assertEquals(5, questions.get(2).durationInMinutes());
        assertEquals(6, questions.get(3).durationInMinutes());


        assertEquals("What is Java?", questions.get(0).description());
        assertEquals("Explain REST APIs.", questions.get(1).description());
        assertEquals("Describe microservices.", questions.get(2).description());
        assertEquals("What is Spring Boot?", questions.get(3).description());
    }
    private Integer parseDuration(String durationStr) {
        if (durationStr == null) return null;
        try {
            String digits = durationStr.replaceAll("\\D+", "");
            return digits.isEmpty() ? null : Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
