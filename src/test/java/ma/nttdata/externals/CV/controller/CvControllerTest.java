package ma.nttdata.externals.CV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.service.cvSrv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ma.nttdata.externals.module.cv.controller.CvController;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CvController.class)
public class CvControllerTest {

    @Mock
    private cvSrv cvSrv;

    @InjectMocks
    private CvController cvController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(cvController).build();
    }

    @Test
    public void extractCandidateInfo_ShouldReturnExtractedData() throws Exception {
        CvFileDTO cvFileDTO = new CvFileDTO(
                "examplePromptCode",
                "exampleBase64String",
                "application/pdf",
                "{\"extracted\":\"data\"}",
                "/path/to/file.pdf");

        when(cvSrv.extractCandidateInfo(cvFileDTO)).thenReturn("extractedData");

        mockMvc.perform(post("/api/v1/cv/extract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(cvFileDTO)))
                .andExpect(status().isOk()) // Vérifier le statut HTTP 200
                .andExpect(jsonPath("$").value("extractedData"));
    }
}