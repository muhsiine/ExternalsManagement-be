package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.commons.services.EmailContentBuilder;
import ma.nttdata.externals.commons.services.EmailService;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.*;
import ma.nttdata.externals.module.interview.service.*;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
    private EmailService emailService;

    @MockitoBean
    private EmailContentBuilder emailContentBuilder;

    @MockitoBean
    private QuestionServ questionServ;

    @MockitoBean
    private EvaluationTypeServ evaluationTypeServ;

    @MockitoBean
    private EvaluationServ evaluationServ;

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
    private InterviewEvaluationPlaceholdersDTO placeholders;


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
        // Given
        InterviewDTO interview1 = new InterviewDTO(
                UUID.randomUUID(),
                LocalDateTime.of(2025, 7, 21, 10, 0),
                LocalDateTime.of(2025, 7, 21, 11, 0),
                "Technical Interview",
                "https://meet.example.com/tech",
                "Strong technical skills",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                15,
                60,
                UUID.randomUUID(),
                UUID.randomUUID(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        InterviewDTO interview2 = new InterviewDTO(
                UUID.randomUUID(),
                LocalDateTime.of(2025, 7, 22, 14, 0),
                LocalDateTime.of(2025, 7, 22, 14, 45),
                "HR Interview",
                "https://meet.example.com/hr",
                "Good communication",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                15,
                60,
                UUID.randomUUID(),
                UUID.randomUUID(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        List<InterviewDTO> interviews = Arrays.asList(interview1, interview2);

        // When
        when(interviewServ.getAllInterviews()).thenReturn(interviews);

        // Then
        mockMvc.perform(get("/api/v1/interviews/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Technical Interview"))
                .andExpect(jsonPath("$[0].link").value("https://meet.example.com/tech"))
                .andExpect(jsonPath("$[0].feedback_general").value("Strong technical skills"))
                .andExpect(jsonPath("$[1].description").value("HR Interview"))
                .andExpect(jsonPath("$[1].link").value("https://meet.example.com/hr"))
                .andExpect(jsonPath("$[1].feedback_general").value("Good communication"));
        verify(interviewServ).getAllInterviews();
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
                .andExpect(jsonPath("$", hasSize(1)))
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
                .andExpect(jsonPath("$", hasSize(1)))
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
        // Arrange
        List<EvaluationDTO> evaluations = List.of(evaluationDTO);

        // When
        when(interviewServ.getEvaluationsOfInterview(interviewId)).thenReturn(evaluations);

        // Then
        mockMvc.perform(get("/api/v1/interviews/{interviewId}/evaluations", interviewId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(evaluationId.toString()))
                .andExpect(jsonPath("$[0].score").value(4.5))
                .andExpect(jsonPath("$[0].feedback").value("Good technical skills"));
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
    void shouldGenerateAndSaveInterviewLink() throws Exception {

        String mockToken = "secure-token-xyz";
        String mockLink = "https://interviews.nttdata.com/interview/" + mockToken;



        when(interviewServ.getInterviewById(interviewId)).thenReturn(interviewDTO);
        when(interviewTokenServ.generateToken(interviewDTO.scheduledAt())).thenReturn(mockToken);
        when(interviewServ.saveInterviewLink(mockToken, interviewId)).thenReturn(mockLink);

        mockMvc.perform(post("/api/v1/interviews/{interviewId}/generateAndSaveLink", interviewId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(mockLink));

    }

    @Test
    @WithMockUser
    void should_send_email() throws Exception {

        SendEmailDTO mockPayload = new SendEmailDTO(
                candidateDTO.fullName(),
                offerDTO.title(),
                "test@gmail.com",
                interviewDTO.scheduledAt(),
                "https://interviews.nttdata.com/interview/abc123"
        );

        given(interviewServ.getEmailInfo(interviewId)).willReturn(mockPayload);

        String expectedHtml = "<html>Email content here</html>";

        given(emailContentBuilder.buildInterviewEmail(
                mockPayload.candidateFullName(),
                mockPayload.offerTitle(),
                mockPayload.link(),
                mockPayload.scheduledDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
        )).willReturn(expectedHtml);

        doNothing().when(emailService).sendEmail(mockPayload.email(), "Your Interview at NTT DATA", expectedHtml);
        mockMvc.perform(post("/api/v1/interviews/{interviewId}/sendEmail", interviewId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));



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

        when(interviewServ.getPlaceholdersForInterviewQuestionsPrompt(interviewId))
                .thenReturn(generateQuestionsInfo);
        when(evaluationTypeServ.findAllById(anyList()))
                .thenReturn(evaluationTypeDTOS);
        when(questionServ.prepareQuestionsFromAIResponse(generateQuestionsInfo,evaluationTypeDTOS))
                .thenReturn(generatedQuestions);
        when(questionServ.saveAllQuestions(anyList()))
                .thenReturn(generatedQuestions);


        String requestBody = """
        {
          "evaluationTypesIds": ["%s", "%s","%s","%s"],
          "numberOfQuestions": %d,
          "estimatedInterviewDuration": %d
        }
        """.formatted(
                evaluationTypeDTOS.get(0).id(),
                evaluationTypeDTOS.get(1).id(),
                evaluationTypeDTOS.get(2).id(),
                evaluationTypeDTOS.get(3).id(),
                numberOfQuestions,
                estimatedDuration
        );

        mockMvc.perform(post("/api/v1/interviews/{interviewId}/generateQuestions", interviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].description").value("What is your experience with Spring Boot?"))
                .andExpect(jsonPath("$[0].durationInMinutes").value(5))
                .andExpect(jsonPath("$[0].interviewId").value(interviewId.toString()))
                .andExpect(jsonPath("$[1].description").value("How do you handle database transactions?"))
                .andExpect(jsonPath("$[1].durationInMinutes").value(4))
                .andExpect(jsonPath("$[2].description").value("Explain microservices architecture."))
                .andExpect(jsonPath("$[2].durationInMinutes").value(6));
        verify(interviewServ).getPlaceholdersForInterviewQuestionsPrompt(interviewId);
        verify(evaluationTypeServ).findAllById(anyList());
        verify(questionServ).prepareQuestionsFromAIResponse(
                eq(generateQuestionsInfo),
                eq(evaluationTypeDTOS)
        );
        verify(questionServ).saveAllQuestions(anyList());
    }

    @Test
    @WithMockUser
    void shouldReturnQuestionsList() throws Exception {


        Question q1 = new Question();
        q1.setId(UUID.randomUUID());
        q1.setDescription("what is spring boot");
        q1.setDurationInMinutes(50);
        Answer answer1 = new Answer();
        answer1.setId(answerId);
        q1.setAnswer(answer1);
        Interview interview = new Interview();
        //interview won't be fetched when using entity because there will be a cycle
        interview.setId(interviewId);
        q1.setInterview(interview);



        List<Question> questions = List.of(q1);

        given(questionServ.findAllQuestionsByInterviewId(interviewId)).willReturn(questions);

        mockMvc.perform(get("/api/v1/interviews/" + interviewId + "/getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("what is spring boot"))
                .andExpect(jsonPath("$[0].durationInMinutes").value(50))
                .andExpect(jsonPath("$[0].answer.id").value(answerId.toString()));;
    }

    @Test
    @WithMockUser
    void shouldReturnQuestionDTOList() throws Exception {


        QuestionDTO dto1 = new QuestionDTO(UUID.randomUUID(), "what is spring boot", 5, interviewId, answerId);
        List<QuestionDTO> dtos = List.of(dto1);

        given(questionServ.findAllQuestionsDTOSByInterviewId(interviewId)).willReturn(dtos);

        mockMvc.perform(get("/api/v1/interviews/" + interviewId + "/getQuestionsDTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("what is spring boot"))
                .andExpect(jsonPath("$[0].durationInMinutes").value(5))
                .andExpect(jsonPath("$[0].interviewId").value(interviewId.toString()))
                .andExpect(jsonPath("$[0].answerId").value(answerId.toString()));
    }



    @Test
    @WithMockUser
    void shouldPrepareAndSaveEvaluation() throws Exception {
        List<QuestionsAndAnswersForEvaluationDTO> questionsAndAnswersForEvaluation = List.of(
                new QuestionsAndAnswersForEvaluationDTO(
                        "Explain how Java handles memory management.",
                        "Java uses automatic garbage collection to manage memory, freeing unused objects.",
                        5,
                        6
                ),
                new QuestionsAndAnswersForEvaluationDTO(
                        "Describe the difference between REST and GraphQL.",
                        "REST uses multiple endpoints, GraphQL uses a single endpoint and allows querying specific fields.",
                        6,
                        5
                ),
                new QuestionsAndAnswersForEvaluationDTO(
                        "What are React Hooks and how do you use them?",
                        "React Hooks let you use state and lifecycle features in functional components like useState and useEffect.",
                        4,
                        5
                )
        );

        List<AiEvaluationResponseDTO> aiEvaluationResponseDTOS = List.of(
                new AiEvaluationResponseDTO(
                        85.5,
                        "Strong understanding of memory concepts. Slightly over time, but accurate and clear.",
                        "Technical Knowledge"
                ),
                new AiEvaluationResponseDTO(
                        78.0,
                        "Clear explanation, but could compare use cases more deeply. Good time management.",
                        "Communication"
                ),
                new AiEvaluationResponseDTO(
                        90.0,
                        "Excellent React knowledge, examples were relevant. Very confident delivery.",
                        "React"
                )
        );

        EvaluationType type1 = new EvaluationType();
        type1.setId(UUID.randomUUID());
        type1.setDescription("Technical Knowledge");
        type1.setCoefficient(1.0);

        EvaluationType type2 = new EvaluationType();
        type2.setId(UUID.randomUUID());
        type2.setDescription("Communication");
        type2.setCoefficient(1.0);

        EvaluationType type3 = new EvaluationType();
        type3.setId(UUID.randomUUID());
        type3.setDescription("React");
        type3.setCoefficient(1.0);

        Evaluation eval1 = new Evaluation();
        eval1.setId(UUID.randomUUID());
        eval1.setScore(85.5);
        eval1.setFeedback("Strong understanding of memory concepts. Slightly over time, but accurate and clear.");
        eval1.setEvaluationType(type1);

        Evaluation eval2 = new Evaluation();
        eval2.setId(UUID.randomUUID());
        eval2.setScore(78.0);
        eval2.setFeedback("Clear explanation, but could compare use cases more deeply. Good time management.");
        eval2.setEvaluationType(type2);

        Evaluation eval3 = new Evaluation();
        eval3.setId(UUID.randomUUID());
        eval3.setScore(90.0);
        eval3.setFeedback("Excellent React knowledge, examples were relevant. Very confident delivery.");
        eval3.setEvaluationType(type3);

        placeholders=  new InterviewEvaluationPlaceholdersDTO(
                candidateDTO,
                offerDTO,
                3,
                15,
                List.of("Technical Knowledge", "Communication", "React"),
                List.of(eval1, eval2, eval3),
                List.of(type1, type2, type3)
        );


        List<Evaluation> savedEvaluations = List.of(
                eval1,eval2,eval3
        );
        when(interviewServ.getInterviewEvaluationPlaceholders(interviewId)).thenReturn(placeholders);
        when(evaluationServ.prepareEvaluationResponseFromAi(questionsAndAnswersForEvaluation, placeholders))
                .thenReturn(aiEvaluationResponseDTOS);
        when(evaluationServ.saveAIEvaluationResponse(aiEvaluationResponseDTOS, placeholders))
                .thenReturn(savedEvaluations);


        List<Double> expectedScores = List.of(85.5, 78.0, 90.0);
        List<String> expectedFeedbacks = List.of(
                "Strong understanding of memory concepts. Slightly over time, but accurate and clear.",
                "Clear explanation, but could compare use cases more deeply. Good time management.",
                "Excellent React knowledge, examples were relevant. Very confident delivery."
        );

        ResultActions result = mockMvc.perform(post("/api/v1/interviews/{interviewId}/evaluation", interviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionsAndAnswersForEvaluation))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(3)));

        for (int i = 0; i < expectedScores.size(); i++) {
            result.andExpect(jsonPath(String.format("$[%d].id", i)).exists())
                    .andExpect(jsonPath(String.format("$[%d].score", i)).value(expectedScores.get(i)))
                    .andExpect(jsonPath(String.format("$[%d].feedback", i)).value(expectedFeedbacks.get(i)));
        }



        verify(interviewServ).getInterviewEvaluationPlaceholders(interviewId);
        verify(evaluationServ).prepareEvaluationResponseFromAi(questionsAndAnswersForEvaluation, placeholders);
        verify(evaluationServ).saveAIEvaluationResponse(aiEvaluationResponseDTOS, placeholders);


    }

    @Test
    @WithMockUser
    void shouldReturnAllEvaluations() throws Exception {
        List<Evaluation> evaluations = List.of(
                new Evaluation(UUID.randomUUID(), 80.0, "Good job", null,new EvaluationType(UUID.randomUUID(), "Technical", 1.0)),
                new Evaluation(UUID.randomUUID(), 75.5, "Needs improvement", null,new EvaluationType(UUID.randomUUID(), "Communication", 1.0))
        );

        when(evaluationServ.getAllEvaluationsByInterviewID(interviewId)).thenReturn(evaluations);

        mockMvc.perform(get("/api/v1/interviews/{interviewId}/evaluation", interviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].score").value(80.0))
                .andExpect(jsonPath("$[0].feedback").value("Good job"))
                .andExpect(jsonPath("$[0].evaluationType.description").value("Technical"))
                .andExpect(jsonPath("$[1].score").value(75.5))
                .andExpect(jsonPath("$[1].feedback").value("Needs improvement"))
                .andExpect(jsonPath("$[1].evaluationType.description").value("Communication"));

        verify(evaluationServ).getAllEvaluationsByInterviewID(interviewId);
    }

    @Test
    @WithMockUser
    void shouldReturnAllEvaluationsDTO() throws Exception {

        List<EvaluationTypeDTO> evaluationTypesDTO = List.of(
                new EvaluationTypeDTO(UUID.randomUUID(),"communication",2.0),
                new EvaluationTypeDTO(UUID.randomUUID(),"Technical",3.0)
        );
        List<EvaluationDTO> evaluationDTOs = List.of(
                new EvaluationDTO(UUID.randomUUID(), 90.0, "Excellent", interviewId,evaluationTypesDTO.get(0).id()),
                new EvaluationDTO(UUID.randomUUID(), 85.0, "Very Good", interviewId,evaluationTypesDTO.get(1).id())
        );



        when(evaluationServ.getAllEvaluationsDTOByInterviewID(interviewId)).thenReturn(evaluationDTOs);

        mockMvc.perform(get("/api/v1/interviews/{interviewId}/evaluationDTO", interviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].score").value(90.0))
                .andExpect(jsonPath("$[0].feedback").value("Excellent"))
                .andExpect(jsonPath("$[0].evaluationTypeId").value(evaluationTypesDTO.get(0).id().toString()))
                .andExpect(jsonPath("$[1].score").value(85.0))
                .andExpect(jsonPath("$[1].feedback").value("Very Good"))
                .andExpect(jsonPath("$[1].evaluationTypeId").value(evaluationTypesDTO.get(1).id().toString()));

        verify(evaluationServ).getAllEvaluationsDTOByInterviewID(interviewId);
    }

}