package ma.nttdata.externals.module.candidate.service.impl;

import ma.nttdata.externals.module.ai.service.LlmService;
import ma.nttdata.externals.module.candidate.constants.MimeType;
import ma.nttdata.externals.module.candidate.constants.JsonExtractionPromptConstants;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.stereotype.Service;

@Service
public class CandidateSrvImpl implements CandidateSrv {

    private final CandidateMapper mapper;
    private final CandidateRepository candidateRepository;
    private final LlmService llmService;
    public CandidateSrvImpl(CandidateMapper candidateMapper, CandidateRepository candidateRepository, LlmService llmService) {
        this.mapper = candidateMapper;
        this.candidateRepository = candidateRepository;
        this.llmService = llmService;
    }
    @Override
    public void save(CandidateDTO candidateDTO) {
        Candidate candidate = mapper.candidateDTOToCandidate(candidateDTO);
        candidateRepository.save(candidate);
    }

    @Override
    public String extractCandidateInfo(String encodedBase64File) {
        return llmService.getJson(JsonExtractionPromptConstants.text, encodedBase64File, MimeType.APPLICATION_PDF.getMimeString(), JsonExtractionPromptConstants.jsonSchema);
    }
}
