package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.service.EvaluationServ;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EvaluationController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationServ evaluationServ;

    @Autowired
    private ObjectMapper objectMapper;

    // ===============================
    // GET ALL
    // ===============================

    @Test
    void shouldGetAllEvaluations() throws Exception {
        when(evaluationServ.getAllEvaluations())
                .thenReturn(List.of(mock(EvaluationDTO.class)));

        mockMvc.perform(get("/api/v1/evaluations"))
                .andExpect(status().isOk());
    }

    // ===============================
    // GET BY ID
    // ===============================

    @Test
    void shouldGetEvaluationById() throws Exception {
        UUID id = UUID.randomUUID();

        when(evaluationServ.getEvaluationById(id))
                .thenReturn(mock(EvaluationDTO.class));

        mockMvc.perform(get("/api/v1/evaluations/{id}", id))
                .andExpect(status().isOk());
    }

    // ===============================
    // CREATE
    // ===============================

    @Test
    void shouldCreateEvaluation() throws Exception {
        when(evaluationServ.createEvaluation(any()))
                .thenReturn(mock(EvaluationDTO.class));

        mockMvc.perform(post("/api/v1/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // UPDATE
    // ===============================

    @Test
    void shouldUpdateEvaluation() throws Exception {
        UUID id = UUID.randomUUID();

        when(evaluationServ.updateEvaluation(any(), any()))
                .thenReturn(mock(EvaluationDTO.class));

        mockMvc.perform(put("/api/v1/evaluations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // DELETE
    // ===============================

    @Test
    void shouldDeleteEvaluation() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(evaluationServ).deleteEvaluation(id);

        mockMvc.perform(delete("/api/v1/evaluations/{id}", id))
                .andExpect(status().isNoContent());
    }
}