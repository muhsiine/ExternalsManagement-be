package ma.nttdata.externals.module.cv.service.impl;

import ma.nttdata.externals.commons.constants.JsonExtractionPromptConstants;
import ma.nttdata.externals.commons.exception.BadRequestException;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.dto.FileDTO;
import ma.nttdata.externals.module.cv.service.CvSrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CvSrvImpl implements CvSrv {

    private static final Logger logger = LoggerFactory.getLogger(CvSrvImpl.class);
    private final boolean mockFlag;
    private final RestClient aiRestClient;

    public CvSrvImpl(@Value("${app.mock.flag}") boolean mockFlag,
                     @Qualifier("aiServiceClient") RestClient aiRestClient) {
        this.mockFlag = mockFlag;
        this.aiRestClient = aiRestClient;
    }

    @Override
    public String extractCandidateInfo(CvFileDTO cvFileDTO) {
        try {
            // Validate input
            if (cvFileDTO == null || cvFileDTO.b64EFile() == null || cvFileDTO.b64EFile().isEmpty()) {
                logger.warn("Invalid CV file data provided");
                throw new BadRequestException("CV file data is required");
            }

            return mockFlag ? JsonExtractionPromptConstants.jsonMock : getExtractedData(cvFileDTO);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error extracting candidate information from CV: {}", e.getMessage(), e);
            throw new InternalServerException("Error processing CV file", e);
        }
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
