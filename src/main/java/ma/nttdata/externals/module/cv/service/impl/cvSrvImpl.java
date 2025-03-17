package ma.nttdata.externals.module.cv.service.impl;

import ma.nttdata.externals.commons.constants.JsonExtractionPromptConstants;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.dto.FileDTO;
import ma.nttdata.externals.module.cv.service.cvSrv;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class cvSrvImpl implements cvSrv {


    private final boolean mockFlag;
    private final RestClient aiRestClient;
    public cvSrvImpl(@Value("${app.mock.flag}") boolean mockFlag,
                     @Qualifier("aiServiceClient") RestClient aiRestClient) {
        this.mockFlag = mockFlag;
        this.aiRestClient = aiRestClient;
    }


    @Override
    public String extractCandidateInfo(CvFileDTO cvFileDTO) {
       return mockFlag ? JsonExtractionPromptConstants.jsonMock : getExtractedData(cvFileDTO);
    }

    private String getExtractedData(CvFileDTO cvFileDTO) {
        var promptText = JsonExtractionPromptConstants.text; // should be obtained from dabase (prompts table using the code)
        var promptSchema = JsonExtractionPromptConstants.jsonSchema; // should be obtained from dabase (prompts table using the code)

        var fileDTO = new FileDTO(promptText, cvFileDTO.b64EFile(),  promptSchema);
        return aiRestClient.post()
                .uri("/extract")
                .body(fileDTO)
                .retrieve()
                .body(String.class);
    }
}
