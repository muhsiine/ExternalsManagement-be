package ma.nttdata.externals.module.candidate.service;

import java.util.List;
import java.util.UUID;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import java.util.Map;

public interface CandidateSrv {
    CandidateDTO save(CandidateDTO candidateDTO);
    CandidateDTO update(UUID id, CandidateDTO candidateDTO);
    List<CandidateDTO> getAllCandidates();
    CandidateDTO getById(UUID id);
    boolean delete(UUID id);
    List<CandidateDTO> getCandidates();

    // New methods
    Map<String, Long> getCandidatesByLanguage();
    Map<String, Long> getCandidatesBySkill();
    Long getTotalCandidates();
}
