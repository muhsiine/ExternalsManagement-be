package ma.nttdata.externals.module.cv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.service.CvSrv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(CvController.class)
@AutoConfigureMockMvc(addFilters = false)
class CvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CvSrv cvSrv;

    @MockBean
    private CandidateSrv candidateSrv;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExtractCandidateInfoSuccessfully() throws Exception {
        CvFileDTO input = new CvFileDTO(
                "PROMPT",
                "base64",
                "application/pdf",
                "{\"name\":\"John\"}",
                "/path/file"
        );

        when(cvSrv.extractCandidateInfo(any()))
                .thenReturn("EXTRACTED_DATA");

        mockMvc.perform(post("/api/v1/cv/extract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("EXTRACTED_DATA"));
    }



    @Test
    void shouldSaveCandidateSuccessfully() throws Exception {
        when(candidateSrv.save(any()))
                .thenReturn(org.mockito.Mockito.mock(CandidateDTO.class));

        mockMvc.perform(post("/api/v1/cv/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // minimal JSON is enough
                .andExpect(status().isCreated());
    }


    @Test
    void shouldReturnInternalServerErrorWhenSaveFails() throws Exception {
        when(candidateSrv.save(any()))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/v1/cv/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error saving candidate")));
    }
}