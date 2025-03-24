package ma.nttdata.externals.module.candidate.service.impl;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.Language;
import ma.nttdata.externals.module.candidate.entity.Skill;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CandidateSrvImpl implements CandidateSrv {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper mapper;

    public CandidateSrvImpl(CandidateRepository candidateRepository, CandidateMapper candidateMapper) {
        this.candidateRepository = candidateRepository;
        this.mapper = candidateMapper;
    }

    @Override
    public CandidateDTO save(CandidateDTO candidateDTO) {
        Candidate candidate = mapper.candidateDTOToCandidate(candidateDTO);
        return mapper.candidateToCandidateDTO(candidateRepository.save(candidate));
    }

    @Override
    public List<CandidateDTO> getCandidates() {
        return candidateRepository.findAll().stream()
                .map(mapper::candidateToCandidateDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getCandidatesByLanguage() {
        return candidateRepository.findAll().stream()
                .flatMap(candidate -> candidate.getLanguages().stream())
                .collect(Collectors.groupingBy(Language::getLanguage, Collectors.counting()));
    }

    @Override
    public Map<String, Long> getCandidatesBySkill() {
        return candidateRepository.findAll().stream()
                .flatMap(candidate -> candidate.getSkills().stream())
                .collect(Collectors.groupingBy(Skill::getSkillName, Collectors.counting()));
    }

    @Override
    public Long getTotalCandidates() {
        return candidateRepository.count();
    }
}