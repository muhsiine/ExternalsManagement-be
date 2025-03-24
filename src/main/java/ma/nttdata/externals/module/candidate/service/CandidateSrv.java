package ma.nttdata.externals.module.candidate.service;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import java.util.List;
import java.util.UUID;

public interface CandidateSrv {
    CandidateDTO save(CandidateDTO candidateDTO);

    List<CandidateDTO> getCandidates();

    CandidateDTO getCandidateById(UUID id);

    CandidateDTO updateCandidate(UUID id, CandidateDTO candidateDTO);

    void deleteCandidate(UUID id);

    List<CandidateDTO> filterCandidates(String skills, String language, Integer yearsOfExperience);
}