package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.TtsRequestDTO;
import ma.nttdata.externals.module.interview.service.QuestionServ;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.mock;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(QuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionServ questionServ;

    @Autowired
    private ObjectMapper objectMapper;

    // ===============================
    // GET ALL
    // ===============================

    @Test
    void shouldGetAllQuestions() throws Exception {
        when(questionServ.getAllQuestions())
                .thenReturn(List.of(mock(QuestionDTO.class)));

        mockMvc.perform(get("/api/v1/questions"))
                .andExpect(status().isOk());
    }

    // ===============================
    // GET BY ID
    // ===============================

    @Test
    void shouldGetQuestionById() throws Exception {
        UUID id = UUID.randomUUID();

        when(questionServ.getQuestionById(id))
                .thenReturn(mock(QuestionDTO.class));

        mockMvc.perform(get("/api/v1/questions/{id}", id))
                .andExpect(status().isOk());
    }

    // ===============================
    // CREATE
    // ===============================

    @Test
    void shouldCreateQuestion() throws Exception {
        when(questionServ.createQuestion(any()))
                .thenReturn(mock(QuestionDTO.class));

        mockMvc.perform(post("/api/v1/questions")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // UPDATE
    // ===============================

    @Test
    void shouldUpdateQuestion() throws Exception {
        UUID id = UUID.randomUUID();

        when(questionServ.updateQuestion(any(), any()))
                .thenReturn(mock(QuestionDTO.class));

        mockMvc.perform(put("/api/v1/questions/{id}", id)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // DELETE
    // ===============================

    @Test
    void shouldDeleteQuestion() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(questionServ).deleteQuestion(id);

        mockMvc.perform(delete("/api/v1/questions/{id}", id))
                .andExpect(status().isNoContent());
    }

    // ===============================
    // GENERATE AUDIO
    // ===============================

    @Test
    void shouldGenerateAudio() throws Exception {
        TtsRequestDTO request = new TtsRequestDTO("Hello question");

        byte[] audio = "audio-data".getBytes();

        when(questionServ.generateInterviewQuestionAudio(any()))
                .thenReturn(ResponseEntity.ok(audio));

        mockMvc.perform(post("/api/v1/questions/generateAudio")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().bytes(audio));
    }
}