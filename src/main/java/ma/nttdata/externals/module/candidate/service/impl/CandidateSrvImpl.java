package ma.nttdata.externals.module.candidate.service.impl;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.City;
import ma.nttdata.externals.module.candidate.entity.Country;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<CandidateDTO> getCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();
        return candidates.stream()
                .map(mapper::candidateToCandidateDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateDTO getCandidateById(UUID id) {
        Optional<Candidate> candidateOptional = candidateRepository.findById(id);
        if (candidateOptional.isPresent()) {
            return mapper.candidateToCandidateDTO(candidateOptional.get());
        } else {
            throw new EntityNotFoundException("Candidate not found with ID: " + id);
        }
    }

    @Override
    public CandidateDTO updateCandidate(UUID id, CandidateDTO candidateDTO) {
        if (!candidateRepository.existsById(id)) {
            throw new EntityNotFoundException("Candidate not found with ID: " + id);
        }

        Candidate candidate = mapper.candidateDTOToCandidate(candidateDTO);
        candidate.setId(id);

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
    public void deleteCandidate(UUID id) {
        if (!candidateRepository.existsById(id)) {
            throw new EntityNotFoundException("Candidate not found with ID: " + id);
        }
        candidateRepository.deleteById(id);
    }

    @Override
    public List<CandidateDTO> filterCandidates(String skills, String language, Integer yearsOfExperience) {
        List<Candidate> candidates = candidateRepository.findAll();
        List<Candidate> filteredCandidates = new ArrayList<>(candidates);

        if (skills != null && !skills.isEmpty()) {
            List<String> skillList = Arrays.asList(skills.split(","));
            filteredCandidates = filteredCandidates.stream()
                    .filter(candidate -> candidate.getSkills().stream()
                            .anyMatch(skill -> skillList.contains(skill.getSkillName())))
                    .collect(Collectors.toList());
        }

        if (language != null && !language.isEmpty()) {
            filteredCandidates = filteredCandidates.stream()
                    .filter(candidate -> candidate.getLanguages().stream()
                            .anyMatch(lang -> lang.getLanguageInEnglish().equalsIgnoreCase(language)))
                    .collect(Collectors.toList());
        }

        if (yearsOfExperience != null) {
            filteredCandidates = filteredCandidates.stream()
                    .filter(candidate -> candidate.getYearsOfExperience() >= yearsOfExperience)
                    .collect(Collectors.toList());
        }

        return filteredCandidates.stream()
                .map(mapper::candidateToCandidateDTO)
                .collect(Collectors.toList());
    }
}