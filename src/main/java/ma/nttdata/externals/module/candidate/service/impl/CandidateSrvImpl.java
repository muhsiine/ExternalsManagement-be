package ma.nttdata.externals.module.candidate.service.impl;

import ma.nttdata.externals.module.ai.service.LlmService;
import ma.nttdata.externals.module.candidate.constants.JsonSchema;
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
        var lang = "French";
        var text  = """
                Extract the data in this file and give it to me in a json form
                take into account that the language of this file is: %s,
                fill the data in this json: %s
                """.formatted(lang, JsonSchema.json)
                ;
        var text2 = "I'll give you this cv/resume and give me the condidates info using this JSON schema: ";
//        return llmService.getJson(text, encodedBase64File, "application/pdf", JsonSchema.jsonSchema);
        return llmService.getJson2(text2, encodedBase64File, "application/pdf", JsonSchema.jsonSchema);
    }
}
