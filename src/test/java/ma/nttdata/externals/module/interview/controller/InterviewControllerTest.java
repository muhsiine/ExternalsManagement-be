package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import ma.nttdata.externals.commons.services.EmailService;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import ma.nttdata.externals.module.interview.service.InterviewTokenServ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.crossstore.ChangeSetPersister;
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

@WebMvcTest(InterviewController.class)// Use WebMvcTest to test only the controller
@Import(TestConfig.class)
class InterviewControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private InterviewServ interviewServ;

        @MockitoBean
        private InterviewTokenServ interviewTokenServ;

         @MockBean
         private EmailService emailService;

        @Autowired
        private ObjectMapper objectMapper;

        private UUID interviewId;
        private UUID candidateId;
        private UUID offerId;
        private UUID questionId;
        private UUID evaluationId;
        private UUID evaluationTypeId;
        private InterviewDTO interviewDTO;
        private QuestionDTO questionDTO;
        private AnswerDTO answerDTO;
        private CandidateDTO candidateDTO;
        private EvaluationDTO evaluationDTO;
        private EvaluationTypeDTO evaluationTypeDTO;

        @BeforeEach
        void setUp() {
                interviewId = UUID.randomUUID();
                candidateId = UUID.randomUUID();
                offerId = UUID.randomUUID();
                questionId = UUID.randomUUID();
                evaluationId = UUID.randomUUID();
                evaluationTypeId = UUID.randomUUID();

                interviewDTO = new InterviewDTO(
                                interviewId,
                                LocalDateTime.of(2025, 7, 21, 9, 0),
                                LocalDateTime.of(2025, 7, 21, 10, 0),
                                "Technical round",
                                "https://zoom.com/meeting",
                                "Very good performance",
                                 LocalDateTime.of(2025, 8, 3, 6, 0),
                        "Candidate showed great problem-solving skills" ,
                                candidateId,
                                offerId);

                questionDTO = new QuestionDTO(
                                questionId,
                                "What is your experience with Java?",
                                15,
                                interviewId);

                answerDTO = new AnswerDTO(
                                UUID.randomUUID(),
                                "I have 5 years of experience with Java",
                                5,
                                questionId);

                candidateDTO = new CandidateDTO(
                                candidateId,
                                "John",
                                LocalDate.now(),
                                3,
                                GenderEnum.M,
                                "Software Engineer",
                                "Hardworking and detail-oriented.",
                                new ArrayList<>(), // contacts
                                new ArrayList<>(), // experiences
                                new ArrayList<>(), // skills
                                new ArrayList<>(), // educations
                                new ArrayList<>(), // cvFiles
                                null, // address
                                new ArrayList<>() // naturalLanguages
                );

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
                        "Candidate showed great problem-solving skills" ,
                                UUID.randomUUID(), // offerId
                                UUID.randomUUID() // candidateId
                );

                InterviewDTO interview2 = new InterviewDTO(
                                UUID.randomUUID(),
                                LocalDateTime.of(2025, 7, 22, 14, 0),
                                LocalDateTime.of(2025, 7, 22, 14, 45),
                                "HR Interview",
                                "https://meet.example.com/hr",
                                "Good communication",
                                LocalDateTime.of(2025, 8, 3, 6, 0),
                        "Candidate showed great problem-solving skills" ,
                                UUID.randomUUID(),
                                UUID.randomUUID()
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
        UUID interviewId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        InterviewDTO interviewDTO = new InterviewDTO(
                interviewId,
                LocalDateTime.of(2025, 7, 22, 14, 0),
                LocalDateTime.of(2025, 7, 22, 15, 0),
                "Technical round",
                "https://zoom.com/meeting",
                "Very good performance",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                offerId,
                candidateId
        );

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
                .andExpect(jsonPath("$.candidateId").value(candidateId.toString()))
                .andExpect(jsonPath("$.offerId").value(offerId.toString()));

        verify(interviewServ).getInterviewById(interviewId);
    }


    @Test
    @WithMockUser
    void createInterview() throws Exception {
        // Define fixed UUIDs
        UUID fixedCandidateId = UUID.fromString("b73be5db-5d04-4be5-b65b-a8210db3db07");
        UUID fixedOfferId = UUID.fromString("a12be5ab-1234-4cdf-b44c-a8210db3abcd");
        UUID fixedInterviewId = UUID.fromString("f99fbb6f-983a-4c89-bf65-589a2926fc01");

        InterviewDTO savedInterviewDTO = new InterviewDTO(
                fixedInterviewId,
                LocalDateTime.of(2025, 7, 22, 14, 0),
                LocalDateTime.of(2025, 7, 22, 15, 0),
                "Technical round",
                "https://meet.example.com/tech",
                "Great candidate",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                fixedOfferId,
                fixedCandidateId
        );

        InterviewDTO inputDto = new InterviewDTO(
                null,
                savedInterviewDTO.startTime(),
                savedInterviewDTO.endTime(),
                savedInterviewDTO.description(),
                savedInterviewDTO.link(),
                savedInterviewDTO.feedback_general(),
                savedInterviewDTO.scheduledAt(),
                savedInterviewDTO.comment(),
                savedInterviewDTO.offerId(),
                savedInterviewDTO.candidateId()
        );

        when(interviewServ.createInterview(any())).thenReturn(savedInterviewDTO);

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
                "Candidate showed great problem-solving skills" ,
                fixedOfferId,
                fixedCandidateId
        );

        when(interviewServ.updateInterview(eq(interviewId), any())).thenReturn(updatedDto);

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
                .andExpect(jsonPath("$.candidateId").value(fixedCandidateId.toString()))
                .andExpect(jsonPath("$.offerId").value(fixedOfferId.toString()));

        verify(interviewServ).updateInterview(eq(interviewId), any());
    }



    @Test
        @WithMockUser
        void deleteInterview() throws Exception {
                doNothing().when(interviewServ).deleteInterview(interviewId);

                mockMvc.perform(delete("/api/v1/interviews/{id}", interviewId)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                                .andExpect(status().isNoContent());

                verify(interviewServ).deleteInterview(interviewId);
        }



        @Test
        @WithMockUser
        void getInterviewsByOfferId() throws Exception {
                // Given
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
                // Given
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
                // Given
                List<AnswerDTO> answers = List.of(answerDTO);

                // When
                when(interviewServ.getAnswersByQuestionId(questionId)).thenReturn(answers);

                // Then
                mockMvc.perform(get("/api/v1/interviews/questions/{questionId}/answer", questionId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                                .andExpect(jsonPath("$[0].description").value("I have 5 years of experience with Java"))
                                .andExpect(jsonPath("$[0].durationInMinutes").value(5));
        }

        @Test
        @WithMockUser
        void getCandidateByInterviewId() throws Exception {
                // When
                when(interviewServ.getCandidateByInterviewId(interviewId)).thenReturn(candidateDTO);

                // Then
                mockMvc.perform(get("/api/v1/interviews/{interviewId}/candidate", interviewId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                                .andExpect(jsonPath("$.fullName").value("John"));
        }

        @Test
        @WithMockUser
        void getEvaluationsOfInterview() throws Exception {
                // Given
                List<EvaluationDTO> evaluations = List.of(evaluationDTO);

                // When
                when(interviewServ.getEvaluationsOfInterview(interviewId)).thenReturn(evaluations);

                // Then
                mockMvc.perform(get("/api/v1/interviews/{interviewId}/evaluations", interviewId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                                .andExpect(jsonPath("$[0].id").value(evaluationId.toString()))
                                .andExpect(jsonPath("$[0].score").value(4.5))
                                .andExpect(jsonPath("$[0].feedback").value("Good technical skills"));
        }

        @Test
        @WithMockUser
        void getEvaluationType() throws Exception {
                // When
                when(interviewServ.getEvaluationTypeOfEvaluation(evaluationId)).thenReturn(evaluationTypeDTO);

                // Then
                mockMvc.perform(get("/api/v1/interviews/{id}/type", evaluationId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(evaluationTypeId.toString()))
                                .andExpect(jsonPath("$.description").value("Technical Skills"))
                                .andExpect(jsonPath("$.coefficient").value(2.0));
        }
}
