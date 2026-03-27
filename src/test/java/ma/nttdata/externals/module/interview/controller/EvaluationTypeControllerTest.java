package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.service.EvaluationTypeServ;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EvaluationTypeController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security
class EvaluationTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationTypeServ evaluationTypeServ;

    @Autowired
    private ObjectMapper objectMapper;

    // ===============================
    // GET ALL
    // ===============================

    @Test
    void shouldGetAllEvaluationTypes() throws Exception {
        when(evaluationTypeServ.getAllTypes())
                .thenReturn(List.of(mock(EvaluationTypeDTO.class)));

        mockMvc.perform(get("/api/v1/evaluation-types"))
                .andExpect(status().isOk());
    }

    // ===============================
    // GET BY ID
    // ===============================

    @Test
    void shouldGetEvaluationTypeById() throws Exception {
        UUID id = UUID.randomUUID();

        when(evaluationTypeServ.getTypeById(id))
                .thenReturn(mock(EvaluationTypeDTO.class));

        mockMvc.perform(get("/api/v1/evaluation-types/{id}", id))
                .andExpect(status().isOk());
    }

    // ===============================
    // CREATE
    // ===============================

    @Test
    void shouldCreateEvaluationType() throws Exception {
        when(evaluationTypeServ.createType(any()))
                .thenReturn(mock(EvaluationTypeDTO.class));

        mockMvc.perform(post("/api/v1/evaluation-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // UPDATE
    // ===============================

    @Test
    void shouldUpdateEvaluationType() throws Exception {
        UUID id = UUID.randomUUID();

        when(evaluationTypeServ.updateType(any(), any()))
                .thenReturn(mock(EvaluationTypeDTO.class));

        mockMvc.perform(put("/api/v1/evaluation-types/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // DELETE
    // ===============================

    @Test
    void shouldDeleteEvaluationType() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(evaluationTypeServ).deleteType(id);

        mockMvc.perform(delete("/api/v1/evaluation-types/{id}", id))
                .andExpect(status().isNoContent());
    }
}