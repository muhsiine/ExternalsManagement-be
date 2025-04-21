package ma.nttdata.externals.module.cv.service.impl;

import ma.nttdata.externals.commons.constants.JsonExtractionPromptConstants;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.dto.FileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CvSrvImplTest {

    @Mock
    private RestClient aiRestClient;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private CvSrvImpl cvSrv;

    @BeforeEach
    void setUp() {
        // Create the service with constructor parameters
        cvSrv = new CvSrvImpl(false, aiRestClient);
    }

    @Test
    void testExtractCandidateInfoWithMockFlag() {
        // Create a new instance with mockFlag = true
        cvSrv = new CvSrvImpl(true, aiRestClient);

        // Create a CV file DTO
        CvFileDTO cvFileDTO = new CvFileDTO(
                "prompt123",
                "base64data",
                "application/pdf",
                "extracted data",
                "path/to/file.pdf"
        );

        // Execute
        String result = cvSrv.extractCandidateInfo(cvFileDTO);

        // Verify
        assertEquals(JsonExtractionPromptConstants.jsonMock, result);
        // Verify that the AI service was not called
        verifyNoInteractions(aiRestClient);
    }

    @Test
    void testExtractCandidateInfoWithAiService() {
        // Setup
        String expectedResponse = "{\"name\":\"John Doe\",\"skills\":[\"Java\",\"Spring\"]}";

        // Create a CV file DTO
        CvFileDTO cvFileDTO = new CvFileDTO(
                "prompt123",
                "base64data",
                "application/pdf",
                "extracted data",
                "path/to/file.pdf"
        );

        // Mock the RestClient chain
        when(aiRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/extract")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FileDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(expectedResponse);

        // Execute
        String result = cvSrv.extractCandidateInfo(cvFileDTO);

        // Verify
        assertEquals(expectedResponse, result);

        // Verify the RestClient was called with the correct parameters
        verify(aiRestClient).post();
        verify(requestBodyUriSpec).uri("/extract");

        // Capture and verify the FileDTO passed to the body method
        ArgumentCaptor<FileDTO> fileDTOCaptor = ArgumentCaptor.forClass(FileDTO.class);
        verify(requestBodySpec).body(fileDTOCaptor.capture());
        FileDTO capturedFileDTO = fileDTOCaptor.getValue();
        assertEquals(cvFileDTO.b64EFile(), capturedFileDTO.b64EFile());
        assertEquals(JsonExtractionPromptConstants.text, capturedFileDTO.text());
        assertEquals(JsonExtractionPromptConstants.jsonSchema, capturedFileDTO.schema());
    }
}
