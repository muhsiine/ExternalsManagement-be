package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.CreateRecordingRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordingDTO;
import ma.nttdata.externals.module.interview.service.RecordingServ;
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
@WebMvcTest(RecordingController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecordingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordingServ recordingServ;

    @Autowired
    private ObjectMapper objectMapper;

    // ===============================
    // CREATE
    // ===============================

    @Test
    void shouldCreateRecord() throws Exception {
        when(recordingServ.createRecordingAndReturnDTO(any()))
                .thenReturn(mock(RecordingDTO.class));

        mockMvc.perform(post("/api/v1/recordings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());
    }

    // ===============================
    // UPDATE SUCCESS
    // ===============================

    @Test
    void shouldUpdateRecord() throws Exception {
        when(recordingServ.updateRecording(any()))
                .thenReturn(mock(RecordingDTO.class));

        mockMvc.perform(put("/api/v1/recordings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    // ===============================
    // UPDATE NOT FOUND
    // ===============================

    @Test
    void shouldReturn404WhenUpdateFails() throws Exception {
        when(recordingServ.updateRecording(any()))
                .thenThrow(new ResourceNotFoundException("not found"));

        mockMvc.perform(put("/api/v1/recordings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    // ===============================
    // GET BY ID SUCCESS
    // ===============================

    @Test
    void shouldFindRecordById() throws Exception {
        UUID id = UUID.randomUUID();

        when(recordingServ.findRecordingById(id))
                .thenReturn(mock(RecordingDTO.class));

        mockMvc.perform(get("/api/v1/recordings/{id}", id))
                .andExpect(status().isOk());
    }

    // ===============================
    // GET BY ID NOT FOUND
    // ===============================

    @Test
    void shouldReturn404WhenRecordNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(recordingServ.findRecordingById(id))
                .thenThrow(new ResourceNotFoundException("not found"));

        mockMvc.perform(get("/api/v1/recordings/{id}", id))
                .andExpect(status().isNotFound());
    }

    // ===============================
    // GET ALL
    // ===============================

    @Test
    void shouldFindAllRecords() throws Exception {
        when(recordingServ.findAllRecordings())
                .thenReturn(List.of(mock(RecordingDTO.class)));

        mockMvc.perform(get("/api/v1/recordings"))
                .andExpect(status().isOk());
    }

    // ===============================
    // GET BY INTERVIEW ID FOUND
    // ===============================

    @Test
    void shouldFindRecordByInterviewId() throws Exception {
        UUID id = UUID.randomUUID();

        when(recordingServ.findRecordingByInterviewId(id))
                .thenReturn(mock(RecordingDTO.class));

        mockMvc.perform(get("/api/v1/recordings/by-interview/{id}", id))
                .andExpect(status().isOk());
    }

    // ===============================
    // GET BY INTERVIEW ID NOT FOUND
    // ===============================

    @Test
    void shouldReturn404WhenInterviewRecordNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(recordingServ.findRecordingByInterviewId(id))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/recordings/by-interview/{id}", id))
                .andExpect(status().isNotFound());
    }

    // ===============================
    // GET BY OFFER ID
    // ===============================

    @Test
    void shouldFindAllRecordsByOfferId() throws Exception {
        UUID id = UUID.randomUUID();

        when(recordingServ.findAllRecordingsByOfferId(id))
                .thenReturn(List.of(mock(RecordingDTO.class)));

        mockMvc.perform(get("/api/v1/recordings/by-offer/{id}", id))
                .andExpect(status().isOk());
    }

    // ===============================
    // DELETE SUCCESS
    // ===============================

    @Test
    void shouldDeleteRecord() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(recordingServ).deleteRecordingById(id);

        mockMvc.perform(delete("/api/v1/recordings/{id}", id))
                .andExpect(status().isNoContent());
    }

    // ===============================
    // DELETE NOT FOUND
    // ===============================

    @Test
    void shouldReturn404WhenDeleteFails() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("not found"))
                .when(recordingServ).deleteRecordingById(id);

        mockMvc.perform(delete("/api/v1/recordings/{id}", id))
                .andExpect(status().isNotFound());
    }
}