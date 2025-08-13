package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.commons.services.EmailContentBuilder;
import ma.nttdata.externals.commons.services.impl.EmailServiceImpl;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.service.*;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewServ interviewServ;

    @MockitoBean
    private InterviewTokenServ interviewTokenServ;

    @MockitoBean
    private EmailServiceImpl emailServiceImpl;

    @MockitoBean
    private EmailContentBuilder emailContentBuilder;

    @MockitoBean
    private InterviewEvaluationUtilServ interviewEvaluationUtilServ;

    @MockitoBean
    private InterviewQuestionsUtilServ interviewQuestionsUtilServ;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID interviewId;
    private UUID candidateId;
    private UUID offerId;
    private UUID questionId;
    private UUID evaluationId;
    private UUID evaluationTypeId;
    private UUID answerId;
    private UUID offerID;
    private InterviewDTO interviewDTO;
    private QuestionDTO questionDTO;
    private AnswerDTO answerDTO;
    private CandidateDTO candidateDTO;
    private EvaluationDTO evaluationDTO;
    private EvaluationTypeDTO evaluationTypeDTO;
    private OfferDTO offerDTO;

    private static String JSON_SCHEMA = """
            [
              {
                "score": "Double - between 0.00 and 100.00",
                "feedback": "String - Try to give an overall feedback of the performance of the candidate in this evaluation type",
                "evaluationTypeDescription": "String - use the exact 'description' field value from the corresponding EvaluationType entity"
              }
            ]
            """;
    private static final String INTERVIEW_EVALUATION_PROMPT = """
            You're an expert interviewing manager and talent acquisition specialist.
            We've passed an interview for an #offer, to a #candidate, and we've gathered the information output and prepared a list of #Question/#answer from that interview,
            I will provide you below the needed information for them.
            Prepare a list of evaluations for that interview, each #evaluation_type is an entry in this list, I will also give you the list of #evaluation_types that we need to evaluate this candidate in.
                                       
            #Take in consideration these instructions:
             - The evaluations must be comprehensive and fair considering the job requirements in #offer_data and the #candidate_data
             - In relevance to the #evaluation_type being assessed, look in the #answers for technical accuracy, depth of knowledge, problem-solving approaches, and communication skills.
             - If the #answer is correct and the time of #answer is lower than the time given in the question, take it into account for positive assessment.
             - Cross-reference #candidate answers with #job requirements to ensure role-specific #evaluation.
             - Take evaluation type #coefficients in consideration
             - Ensure fairness by matching evaluation difficulty and accuracy to candidate's stated experience level
             - Use the exact 'description' value from each EvaluationTypes listFor the 'evaluationType' field in the output.
             - Ensure each evaluation in the output array corresponds exactly to one EvaluationType from the input list.
             - Do not skip any evaluation types or add additional ones not provided in the Evaluation Types.
             - Reference specific technologies, skills, or experiences mentioned in the candidate profile when relevant.
             - Use simple language: A2-B1-B2
             - Return ONLY a valid JSON array with exactly this structure, no additional text or formatting:"{JSON_SCHEMA}", here you have a mock example:"{JSON_MOCK}".
                                       
            I provide bellow the needed information:
             - #Candidate Profile: "{CANDIDATE_DATA}",
             - #Job Offer requirements: "{OFFER_DATA}",
             - #Evaluation Types criteria: "{EVALUATION_TYPES_DATA}".
             - #Questions And Answers with the estimated answer time and the real answer time: "{QuestionAnswer_DATA}"
            """;

    @BeforeEach
    void setUp() {
        interviewId = UUID.randomUUID();
        candidateId = UUID.randomUUID();
        offerId = UUID.randomUUID();
        questionId = UUID.randomUUID();
        evaluationId = UUID.randomUUID();
        evaluationTypeId = UUID.randomUUID();
        answerId = UUID.randomUUID();
        offerId = UUID.randomUUID();

        evaluationDTO = new EvaluationDTO(
                evaluationId,
                4.5,
                "Good technical skills",
                interviewId,
                evaluationTypeId);

        evaluationTypeDTO = new EvaluationTypeDTO(
                evaluationTypeId,
                "Technical Skills",
                2.0);

        questionDTO = new QuestionDTO(
                questionId,
                "What is your experience with Java?",
                15,
                interviewId,
                answerId);

        answerDTO = new AnswerDTO(
                answerId,
                "I have 5 years of experience with Java",
                5);

        interviewDTO = new InterviewDTO(
                interviewId,
                LocalDateTime.of(2025, 7, 21, 9, 0),
                LocalDateTime.of(2025, 7, 21, 10, 0),
                "Technical round",
                "https://zoom.com/meeting",
                "Very good performance",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                15,
                60,
                candidateId,
                offerId,
                List.of(evaluationDTO),
                List.of(questionDTO));

        candidateDTO = new CandidateDTO(
                candidateId,
                "John",
                LocalDate.of(1990, 5, 15),
                3,
                GenderEnum.M,
                "Java",
                "Hardworking and detail-oriented.",
                Collections.emptyList(), // contacts
                Collections.emptyList(), // experiences
                Collections.emptyList(), // skills
                Collections.emptyList(), // educations
                Collections.emptyList(), // cvFiles
                null, // address
                Collections.emptyList(), // naturalLanguages
                List.of(interviewDTO) // interviews
        );

        offerDTO = new OfferDTO(
                offerId,
                "Backend Engineer",
                "We need a backend engineer with solid experience in Node js and spring boot",
                List.of(interviewDTO)
        );
    }

    @Test
    @WithMockUser
    void testGetAllInterviews() throws Exception {

        InterviewListDTO interview1 = new InterviewListDTO(
                UUID.randomUUID(),
                LocalDateTime.of(2025, 7, 21, 10, 0),
                LocalDateTime.of(2025, 7, 21, 11, 0),
                "Technical Interview",
                "https://meet.example.com/tech",
                "Strong technical skills",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                "test1 test1",
                "Node js",
                "Backend Developer"
        );

        InterviewListDTO interview2 = new InterviewListDTO(
                UUID.randomUUID(),
                LocalDateTime.of(2025, 7, 22, 14, 0),
                LocalDateTime.of(2025, 7, 22, 14, 45),
                "HR Interview",
                "https://meet.example.com/hr",
                "Good communication",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                "test test",
                "React",
                "Frontend developer"
        );

        List<InterviewListDTO> interviews = Arrays.asList(interview1, interview2);

        // When
        when(interviewServ.getAllInterviewList()).thenReturn(interviews);

        // Then
        mockMvc.perform(get("/api/v1/interviews")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Technical Interview"))
                .andExpect(jsonPath("$[0].link").value("https://meet.example.com/tech"))
                .andExpect(jsonPath("$[0].feedback_general").value("Strong technical skills"))
                .andExpect(jsonPath("$[1].description").value("HR Interview"))
                .andExpect(jsonPath("$[1].link").value("https://meet.example.com/hr"))
                .andExpect(jsonPath("$[1].feedback_general").value("Good communication"));
        verify(interviewServ).getAllInterviewList();
    }

    @Test
    @WithMockUser
    void getInterviewById() throws Exception {
        // Arrange
        when(interviewServ.getInterviewById(interviewId)).thenReturn(interviewDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/interviews/{id}", interviewId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(interviewId.toString()))
                .andExpect(jsonPath("$.description").value("Technical round"))
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists())
                .andExpect(jsonPath("$.link").value("https://zoom.com/meeting"))
                .andExpect(jsonPath("$.feedback_general").value("Very good performance"))
                .andExpect(jsonPath("$.candidateId").value(interviewDTO.candidateId().toString()))
                .andExpect(jsonPath("$.offerId").value(interviewDTO.offerId().toString())); // Fixed to use interviewDTO.offerId()

        verify(interviewServ).getInterviewById(interviewId);
    }
    @Test
    @WithMockUser
    void createInterview() throws Exception {
        // Arrange
        UUID fixedCandidateId = UUID.fromString("b73be5db-5d04-4be5-b65b-a8210db3db07");
        UUID fixedOfferId = UUID.fromString("a12be5ab-1234-4cdf-b44c-a8210db3abcd");
        UUID fixedInterviewId = UUID.fromString("f99fbb6f-983a-4c89-bf65-589a2926fc01");

        InterviewDTO inputDto = new InterviewDTO(
                null,
                LocalDateTime.of(2025, 7, 22, 14, 0),
                LocalDateTime.of(2025, 7, 22, 15, 0),
                "Technical round",
                "https://meet.example.com/tech",
                "Great candidate",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                15,
                60,
                fixedCandidateId,
                fixedOfferId,
                Collections.emptyList(),
                Collections.emptyList()
        );

        InterviewDTO savedInterviewDTO = new InterviewDTO(
                fixedInterviewId,
                inputDto.startTime(),
                inputDto.endTime(),
                inputDto.description(),
                inputDto.link(),
                inputDto.feedback_general(),
                inputDto.scheduledAt(),
                inputDto.comment(),
                15,
                60,
                inputDto.candidateId(),
                inputDto.offerId(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(interviewServ.createInterview(any())).thenReturn(savedInterviewDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fixedInterviewId.toString()))
                .andExpect(jsonPath("$.description").value("Technical round"))
                .andExpect(jsonPath("$.candidateId").value(fixedCandidateId.toString()))
                .andExpect(jsonPath("$.offerId").value(fixedOfferId.toString()))
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists())
                .andExpect(jsonPath("$.link").value("https://meet.example.com/tech"))
                .andExpect(jsonPath("$.feedback_general").value("Great candidate"));

        verify(interviewServ).createInterview(any());
    }

    @Test
    @WithMockUser
    void updateInterview() throws Exception {
        // Arrange
        UUID fixedCandidateId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID fixedOfferId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        InterviewDTO updatedDto = new InterviewDTO(
                interviewId,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Updated description",
                "https://meet.example.com/interview",
                "Updated feedback",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                15,
                60,
                fixedCandidateId,
                fixedOfferId,
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(interviewServ.updateInterview(eq(interviewId), any())).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/api/v1/interviews/{id}", interviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(interviewId.toString()))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.feedback_general").value("Updated feedback"))
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists())
                .andExpect(jsonPath("$.link").exists())
                .andExpect(jsonPath("$.candidateId").value(updatedDto.candidateId().toString())) // Fixed to use updatedDto.candidateId()
                .andExpect(jsonPath("$.offerId").value(updatedDto.offerId().toString())); // Updated to use updatedDto.offerId() for consistency

        verify(interviewServ).updateInterview(eq(interviewId), any());
    }

    @Test
    @WithMockUser
    void deleteInterview() throws Exception {
        // Arrange
        doNothing().when(interviewServ).deleteInterview(interviewId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/interviews/{id}", interviewId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(interviewServ).deleteInterview(interviewId);
    }

    @Test
    @WithMockUser
    void getInterviewsByOfferId() throws Exception {
        // Arrange
        List<InterviewDTO> interviews = List.of(interviewDTO);

        // When
        when(interviewServ.getInterviewsByOfferId(offerId)).thenReturn(interviews);

        // Then
        mockMvc.perform(get("/api/v1/interviews/offer/{offerId}", offerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(interviewId.toString()))
                .andExpect(jsonPath("$[0].description").value("Technical round"));
    }

    @Test
    @WithMockUser
    void getQuestionsByInterviewId() throws Exception {
        // Arrange
        List<QuestionDTO> questions = List.of(questionDTO);

        // When
        when(interviewServ.getQuestionsByInterviewId(interviewId)).thenReturn(questions);

        // Then
        mockMvc.perform(get("/api/v1/interviews/{interviewId}/questions", interviewId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(questionId.toString()))
                .andExpect(jsonPath("$[0].description").value("What is your experience with Java?"))
                .andExpect(jsonPath("$[0].durationInMinutes").value(15));
    }

    @Test
    @WithMockUser
    void getAnswersByQuestion() throws Exception {
        // Arrange
        when(interviewServ.getAnswerByQuestionId(questionId)).thenReturn(answerDTO);

        // Then
        mockMvc.perform(get("/api/v1/interviews/questions/{questionId}/answer", questionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("I have 5 years of experience with Java"))
                .andExpect(jsonPath("$.durationInMinutes").value(5));
    }
    @Test
    @WithMockUser
    void getCandidateByInterviewId() throws Exception {
        // Arrange
        when(interviewServ.getCandidateByInterviewId(interviewId)).thenReturn(candidateDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/interviews/{interviewId}/candidate", interviewId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John"))
                .andExpect(jsonPath("$.mainTech").value("Java"))
                .andExpect(jsonPath("$.summary").value("Hardworking and detail-oriented."));
    }

    @Test
    @WithMockUser
    void getEvaluationsOfInterview() throws Exception {
        EvaluationTypeDTO evaluationType1 = new EvaluationTypeDTO(
                UUID.randomUUID(),
                "Technical Skills",
                1.5
        );

        EvaluationTypeDTO evaluationType2 = new EvaluationTypeDTO(
                UUID.randomUUID(),
                "Communication",
                1.0
        );

        FullEvaluationDTO evaluation1 = new FullEvaluationDTO(
                UUID.randomUUID(),
                4.7,
                "Excellent technical knowledge",
                interviewId,
                evaluationType1
        );

        FullEvaluationDTO evaluation2 = new FullEvaluationDTO(
                UUID.randomUUID(),
                3.8,
                "Good communication skills but can improve",
                interviewId,
                evaluationType2
        );

        InterviewEvaluationDTO interviewEvaluation = new InterviewEvaluationDTO(
                interviewId,
                "Alice Smith",
                "Backend Engineer",
                LocalDateTime.of(2025, 9, 10, 14, 0),
                60,
                List.of(evaluation1, evaluation2)
        );

        when(interviewEvaluationUtilServ.getInterviewEvaluations(interviewId)).thenReturn(interviewEvaluation);


        mockMvc.perform(get("/api/v1/interviews/{interviewId}/evaluations", interviewId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interviewId").value(interviewId.toString()))
                .andExpect(jsonPath("$.candidateFullName").value("Alice Smith"))
                .andExpect(jsonPath("$.offerTitle").value("Backend Engineer"))
                .andExpect(jsonPath("$.evaluations", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.evaluations[0].score").value(4.7))
                .andExpect(jsonPath("$.evaluations[0].feedback").value("Excellent technical knowledge"))
                .andExpect(jsonPath("$.evaluations[1].score").value(3.8))
                .andExpect(jsonPath("$.evaluations[1].feedback").value("Good communication skills but can improve"));

    }

    @Test
    @WithMockUser
    void getEvaluationType() throws Exception {
        // Arrange
        when(interviewServ.getEvaluationTypeOfEvaluation(evaluationId)).thenReturn(evaluationTypeDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/interviews/{id}/type", evaluationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(evaluationTypeId.toString()))
                .andExpect(jsonPath("$.description").value("Technical Skills"))
                .andExpect(jsonPath("$.coefficient").value(2.0));
    }

    @Test
    @WithMockUser
    void generateInterviewQuestions() throws Exception {
        int numberOfQuestions = 5;
        int estimatedDuration = 30;

        placeholdersForInterviewQuestionsPromptDTO generateQuestionsInfo = new placeholdersForInterviewQuestionsPromptDTO(
                candidateDTO,
                offerDTO,
                numberOfQuestions,
                estimatedDuration
        );

        List<QuestionDTO> generatedQuestions = Arrays.asList(
                new QuestionDTO(
                        UUID.randomUUID(),
                        "What is your experience with Spring Boot?",
                        5,
                        interviewId,
                        null
                ),
                new QuestionDTO(
                        UUID.randomUUID(),
                        "How do you handle database transactions?",
                        4,
                        interviewId,
                        null
                ),
                new QuestionDTO(
                        UUID.randomUUID(),
                        "Explain microservices architecture.",
                        6,
                        interviewId,
                        null
                )
        );



        List<EvaluationTypeDTO> evaluationTypeDTOS = List.of(
                new EvaluationTypeDTO(UUID.randomUUID(), "Technical Skills", 1.5),
                new EvaluationTypeDTO(UUID.randomUUID(), "Communication", 1.0),
                new EvaluationTypeDTO(UUID.randomUUID(), "Problem Solving", 2.0),
                new EvaluationTypeDTO(UUID.randomUUID(), "Teamwork", 1.2),
                new EvaluationTypeDTO(UUID.randomUUID(), "Creativity", 0.8)
        );

        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),"test",INTERVIEW_EVALUATION_PROMPT,JSON_SCHEMA);


        when(interviewQuestionsUtilServ.generateInterviewQuestions(eq(interviewId),any(GenerateInterviewQuestionsRequest.class)))
                .thenReturn(generatedQuestions);

        String requestBody = """
        {
          "promptCode": "%s",
          "evaluationTypesIds": ["%s", "%s","%s","%s"]
          
        }
        """.formatted(
                prompt.promptCode(),
                evaluationTypeDTOS.get(0).id(),
                evaluationTypeDTOS.get(1).id(),
                evaluationTypeDTOS.get(2).id(),
                evaluationTypeDTOS.get(3).id()
        );

        mockMvc.perform(post("/api/v1/interviews/{interviewId}/generateQuestions", interviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].description").value("What is your experience with Spring Boot?"))
                .andExpect(jsonPath("$[0].durationInMinutes").value(5))
                .andExpect(jsonPath("$[0].interviewId").value(interviewId.toString()))
                .andExpect(jsonPath("$[1].description").value("How do you handle database transactions?"))
                .andExpect(jsonPath("$[1].durationInMinutes").value(4))
                .andExpect(jsonPath("$[2].description").value("Explain microservices architecture."))
                .andExpect(jsonPath("$[2].durationInMinutes").value(6));

    }

    @Test
    @WithMockUser
    void should_return_evaluation_is_created_and_saved() throws Exception {
        QuestionsAndAnswersForEvaluationDTO questionsAndAnswersForEvaluation1 =
                new QuestionsAndAnswersForEvaluationDTO(
                        "what is the useState hook",
                         "use state is",
                        2,
                            3);
        QuestionsAndAnswersForEvaluationDTO questionsAndAnswersForEvaluation2 =
                new QuestionsAndAnswersForEvaluationDTO(
                        "what is the useEffect hook",
                        "useEffect is",
                        2,
                        3);

        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),"test",INTERVIEW_EVALUATION_PROMPT,JSON_SCHEMA);

        InterviewEvaluationsRequestDTO interviewEvaluationsRequest = new InterviewEvaluationsRequestDTO("test",List.of(questionsAndAnswersForEvaluation1, questionsAndAnswersForEvaluation2));

        Evaluation evaluation = new Evaluation();
        evaluation.setFeedback("nothing");
        evaluation.setScore(60.0);
        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(UUID.randomUUID());
        evaluationType.setCoefficient(3.0);
        evaluationType.setDescription("react skills");
        Interview interview = new Interview();
        interview.setId(interviewId);
        evaluation.setInterview(interview);
        evaluation.setEvaluationType(evaluationType);
        String jsonRequest = new ObjectMapper().writeValueAsString(interviewEvaluationsRequest);


        when(interviewEvaluationUtilServ.prepareInterviewEvaluation(interviewId,interviewEvaluationsRequest)).thenReturn(List.of(evaluation));

        mockMvc.perform(post("/api/v1/interviews/{interviewId}/evaluations",interviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Evaluation is created and saved"));

        verify(interviewEvaluationUtilServ).prepareInterviewEvaluation(interviewId,interviewEvaluationsRequest);
    }


}