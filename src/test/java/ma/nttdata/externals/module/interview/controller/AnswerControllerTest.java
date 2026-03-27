package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.interview.dto.AnswerDTO;
import ma.nttdata.externals.module.interview.service.AnswerServ;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.mock;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AnswerController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security
class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnswerServ answerServ;

    @Autowired
    private ObjectMapper objectMapper;

    // ===============================
    // GET ALL
    // ===============================

    @Test
    void shouldGetAllAnswers() throws Exception {
        when(answerServ.getAllAnswers())
                .thenReturn(List.of(mock(AnswerDTO.class)));

        mockMvc.perform(get("/api/v1/answers"))
                .andExpect(status().isOk());
    }

    // ===============================
    // GET BY ID
    // ===============================

    @Test
    void shouldGetAnswerById() throws Exception {
        UUID id = UUID.randomUUID();

        when(answerServ.getAnswerById(id))
                .thenReturn(mock(AnswerDTO.class));

        mockMvc.perform(get("/api/v1/answers/{id}", id))
                .andExpect(status().isOk());
    }

    // ===============================
    // CREATE
    // ===============================

    @Test
    void shouldCreateAnswer() throws Exception {
        when(answerServ.createAnswer(any()))
                .thenReturn(mock(AnswerDTO.class));

        mockMvc.perform(post("/api/v1/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // UPDATE
    // ===============================

    @Test
    void shouldUpdateAnswer() throws Exception {
        UUID id = UUID.randomUUID();

        when(answerServ.updateAnswer(any(), any()))
                .thenReturn(mock(AnswerDTO.class));

        mockMvc.perform(put("/api/v1/answers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // DELETE
    // ===============================

    @Test
    void shouldDeleteAnswer() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(answerServ).deleteAnswer(id);

        mockMvc.perform(delete("/api/v1/answers/{id}", id))
                .andExpect(status().isNoContent());
    }
}