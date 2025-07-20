package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewServ interviewServ;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID interviewId;
    private UUID candidateId;
    private UUID offerId;
    private InterviewDTO interviewDTO;

    @BeforeEach
    void setUp() {
        interviewId = UUID.randomUUID();
        candidateId = UUID.randomUUID();
        offerId = UUID.randomUUID();

        interviewDTO = new InterviewDTO(
                interviewId,
                LocalDateTime.of(2025, 7, 21, 9, 0),
                LocalDateTime.of(2025, 7, 21, 10, 0),
                "Technical round",
                "https://zoom.com/meeting",
                "Very good performance",
                candidateId,
                offerId
        );
    }

    @Test
    void getAllInterviews() throws Exception {
        Mockito.when(interviewServ.getAllInterviews()).thenReturn(List.of(interviewDTO));

        mockMvc.perform(get("/api/v1/interviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(interviewId.toString()));
    }

    @Test
    void getInterviewById() throws Exception {
        Mockito.when(interviewServ.getInterviewById(interviewId)).thenReturn(interviewDTO);

        mockMvc.perform(get("/api/v1/interviews/{id}", interviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(interviewId.toString()))
                .andExpect(jsonPath("$.description").value("Technical round"));
    }

    @Test
    void createInterview() throws Exception {
        InterviewDTO inputDto = new InterviewDTO(
                null,
                interviewDTO.startTime(),
                interviewDTO.endTime(),
                interviewDTO.description(),
                interviewDTO.link(),
                interviewDTO.feedback_general(),
                interviewDTO.candidateId(),
                interviewDTO.offerId()
        );

        Mockito.when(interviewServ.createInterview(any())).thenReturn(interviewDTO);

        mockMvc.perform(post("/api/v1/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Technical round"))
                .andExpect(jsonPath("$.candidateId").value(candidateId.toString()))
                .andExpect(jsonPath("$.offerId").value(offerId.toString()));
    }

    @Test
    void updateInterview() throws Exception {
        InterviewDTO updatedDto = new InterviewDTO(
                interviewDTO.id(),
                interviewDTO.startTime(),
                interviewDTO.endTime(),
                "Updated description",
                interviewDTO.link(),
                "Updated feedback",
                interviewDTO.candidateId(),
                interviewDTO.offerId()
        );

        Mockito.when(interviewServ.updateInterview(any(), any())).thenReturn(updatedDto);

        mockMvc.perform(put("/api/v1/interviews/{id}", interviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.feedback_general").value("Updated feedback"));
    }

    @Test
    void deleteInterview() throws Exception {
        mockMvc.perform(delete("/api/v1/interviews/{id}", interviewId))
                .andExpect(status().isOk());

        Mockito.verify(interviewServ).deleteInterview(interviewId);
    }
}
