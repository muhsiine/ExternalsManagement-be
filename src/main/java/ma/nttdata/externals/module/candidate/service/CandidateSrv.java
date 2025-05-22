package ma.nttdata.externals.module.candidate.service;

import java.util.List;
import java.util.UUID;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import java.util.Map;

public interface CandidateSrv {

    CandidateDTO update(UUID id, CandidateDTO candidateDTO);
    List<CandidateDTO> getAllCandidates();
    CandidateDTO getById(UUID id);
    boolean delete(UUID id);
    List<CandidateDTO> getCandidates();

    // New methods
    Map<String, Long> getCandidatesByLanguage();
    Long getTotalCandidates();

    //New methods
    CandidateDTO save(CandidateDTO candidateDTO);
    List<String> getAllTechnologies();
    public List<CandidateDTO> getCandidatesByLanguage(String language);
    List<CandidateDTO> getCandidatesBySkill(String skill);

    // Overloaded method for tests - returns all candidates (needed for the test compatibility)
    List<CandidateDTO> getCandidatesBySkill();

    Map<String, Long> getCandidatesBySkillCount();
}
