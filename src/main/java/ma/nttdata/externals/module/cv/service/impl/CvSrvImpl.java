package ma.nttdata.externals.module.cv.service.impl;

import ma.nttdata.externals.commons.constants.JsonExtractionPromptConstants;
import ma.nttdata.externals.commons.exception.BadRequestException;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import ma.nttdata.externals.module.cv.dto.FileDTO;
import ma.nttdata.externals.module.cv.service.CvSrv;
import ma.nttdata.externals.module.prompt.constants.PromptEnum;
import ma.nttdata.externals.module.prompt.repository.PromptRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CvSrvImpl implements CvSrv {
    private final boolean mockFlag;
    private final RestClient aiRestClient;
    private final PromptRepository promptRepository;

    public CvSrvImpl(@Value("${app.mock.flag}") boolean mockFlag,
                     @Qualifier("aiServiceClient") RestClient aiRestClient,
                     PromptRepository promptRepository) {
        this.mockFlag = mockFlag;
        this.aiRestClient = aiRestClient;
        this.promptRepository = promptRepository;
    }

    @Override
    public String extractCandidateInfo(CvFileDTO cvFileDTO) {
        try {
            // Validate input
            if (cvFileDTO == null || cvFileDTO.b64EFile() == null || cvFileDTO.b64EFile().isEmpty()) {
                throw new BadRequestException("CV file data is required");
            }

            return mockFlag ? JsonExtractionPromptConstants.jsonMock : getExtractedData(cvFileDTO);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error processing CV file", e);
        }
    }

    private String getExtractedData(CvFileDTO cvFileDTO) {
        var prompt = promptRepository.findByPromptCode(PromptEnum.CV_EXTRACTION.name())
                .orElseThrow(() -> new InternalServerException("Prompt with code '" + PromptEnum.CV_EXTRACTION.name() + "' not found"));

        var promptText = prompt.getPromptDesc();
        var promptSchema = prompt.getSchema();

        var fileDTO = new FileDTO(promptText, cvFileDTO.b64EFile(),  promptSchema);
        return aiRestClient.post()
                .uri("/extract")
                .body(fileDTO)
                .retrieve()
                .body(String.class);
    }
}
