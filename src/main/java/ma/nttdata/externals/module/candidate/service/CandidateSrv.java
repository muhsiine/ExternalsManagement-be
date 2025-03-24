package ma.nttdata.externals.module.candidate.service;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import java.util.List;
import java.util.Map;

public interface CandidateSrv {
    CandidateDTO save(CandidateDTO candidateDTO);
    List<CandidateDTO> getCandidates();

    // New methods
    Map<String, Long> getCandidatesByLanguage();
    Map<String, Long> getCandidatesBySkill();
    Long getTotalCandidates();
}
