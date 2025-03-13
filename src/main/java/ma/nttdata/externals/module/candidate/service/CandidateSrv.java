package ma.nttdata.externals.module.candidate.service;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;

public interface CandidateSrv {
    void save(CandidateDTO candidateDTO);

    String extractCandidateInfo(String encodedBase64File);
}
