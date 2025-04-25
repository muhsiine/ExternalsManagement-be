package ma.nttdata.externals.module.candidate.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CandidateSrvImpl implements CandidateSrv {

    private final CandidateMapper mapper;
    private final CandidateRepository candidateRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    public CandidateSrvImpl(CandidateMapper candidateMapper,
                            CandidateRepository candidateRepository,
                            CountryRepository countryRepository,
                            CityRepository cityRepository) {
        this.mapper = candidateMapper;
        this.candidateRepository = candidateRepository;
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public CandidateDTO save(CandidateDTO candidateDTO) {
        Candidate candidate = mapper.candidateDTOToCandidate(candidateDTO);
        Optional<Country> existingCountry = countryRepository.findByEnglishName(candidate.getAddress().getCountry().getEnglishName());
        existingCountry.ifPresent(country -> {
            candidate.getAddress().setCountry(country);
            candidate.getAddress().getCity().setCountry(country);
        });
        Optional<City> existingCity = cityRepository.findByName(candidate.getAddress().getCity().getName());
        existingCity.ifPresent(candidate.getAddress()::setCity);
        return mapper.candidateToCandidateDTO(candidateRepository.save(candidate));
    }

    @Override
    @Transactional
    public CandidateDTO update(UUID id, CandidateDTO candidateDTO) {
        Optional<Candidate> existingCandidateOpt = candidateRepository.findById(id);
        if (existingCandidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found with id :" + id);
        }
        Candidate existingCandidate = existingCandidateOpt.get();
        Candidate updatedCandidate = mapper.candidateDTOToCandidate(candidateDTO);
        Candidate savedCandidate = candidateRepository.save(updatedCandidate);
        return mapper.candidateToCandidateDTO(savedCandidate);
    }

    @Override
    public List<CandidateDTO> getAllCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();
        return candidates.stream()
                .map(mapper::candidateToCandidateDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateDTO getById(UUID id) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        return candidate.map(mapper::candidateToCandidateDTO).orElseThrow(() -> new EntityNotFoundException("Candidate id not found : " + id));
    }


    @Override
    public boolean delete(UUID id) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        if (candidate.isEmpty()) {
            throw new RuntimeException("Candidate id not found : " + id);
        }
        candidateRepository.deleteById(id);
        return true;
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