package ma.nttdata.externals.module.candidate.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.nttdata.externals.commons.exception.BadRequestException;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import ma.nttdata.externals.module.candidate.repository.LanguageRepository;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CandidateSrvImpl implements CandidateSrv {
    private final CandidateMapper mapper;
    private final CandidateRepository candidateRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final LanguageRepository languageRepository;
    private final CandidateMapper candidateMapper;

    public CandidateSrvImpl(CandidateMapper candidateMapper,
                            CandidateRepository candidateRepository,
                            CountryRepository countryRepository,
                            CityRepository cityRepository,
                            LanguageRepository languageRepository) {
        this.mapper = candidateMapper;
        this.candidateRepository = candidateRepository;
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
        this.languageRepository = languageRepository;
        this.candidateMapper = candidateMapper;
    }

    @Override
    public CandidateDTO save(CandidateDTO candidateDTO) {
        try {
            if (candidateDTO == null) {
                throw new BadRequestException("Candidate data is required");
            }

            Candidate candidate = mapper.candidateDTOToCandidate(candidateDTO);
            Optional<Country> existingCountry = countryRepository.findByEnglishName(candidate.getAddress().getCountry().getEnglishName());
            existingCountry.ifPresent(country -> {
                candidate.getAddress().setCountry(country);
                candidate.getAddress().getCity().setCountry(country);
            });
            Optional<City> existingCity = cityRepository.findByName(candidate.getAddress().getCity().getName());
            existingCity.ifPresent(candidate.getAddress()::setCity);

            Candidate savedCandidate = candidateRepository.save(candidate);
            return mapper.candidateToCandidateDTO(savedCandidate);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error creating candidate", e);
        }
    }

    @Override
    @Transactional
    public CandidateDTO update(UUID id, CandidateDTO candidateDTO) {
        try {
            if (id == null) {
                throw new BadRequestException("Candidate ID is required");
            }
            if (candidateDTO == null) {
                throw new BadRequestException("Candidate data is required");
            }
            Optional<Candidate> existingCandidateOpt = candidateRepository.findById(id);
            if (existingCandidateOpt.isEmpty()) {
                throw new ResourceNotFoundException("Candidate", id);
            }
            Candidate existingCandidate = existingCandidateOpt.get();
            mapper.updateCandidateFromDTO(candidateDTO, existingCandidate);
            Candidate savedCandidate = candidateRepository.save(existingCandidate);
            return mapper.candidateToCandidateDTO(savedCandidate);
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error updating candidate", e);
        }
    }

    @Override
    public List<CandidateDTO> getAllCandidates() {
        try {
            List<Candidate> candidates = candidateRepository.findAll();

            if (candidates.isEmpty()) {
                throw new ResourceNotFoundException("No candidates found");
            }

            return candidates.stream()
                    .map(mapper::candidateToCandidateDTO)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error retrieving candidates", e);
        }
    }

    @Override
    public CandidateDTO getById(UUID id) {
        try {
            if (id == null) {
                throw new BadRequestException("Candidate ID is required");
            }

            Optional<Candidate> candidate = candidateRepository.findById(id);
            if (candidate.isEmpty()) {
                throw new EntityNotFoundException("Candidate id not found : " + id);
            }

            return mapper.candidateToCandidateDTO(candidate.get());
        } catch (EntityNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error retrieving candidate", e);
        }
    }

    @Override
    public boolean delete(UUID id) {
        try {
            if (id == null) {
                throw new BadRequestException("Candidate ID is required");
            }

            Optional<Candidate> candidate = candidateRepository.findById(id);
            if (candidate.isEmpty()) {
                throw new ResourceNotFoundException("Candidate", id);
            }

            candidateRepository.deleteById(id);
            return true;
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error deleting candidate", e);
        }
    }

    @Override
    public List<CandidateDTO> getCandidates() {
        try {
            List<Candidate> candidates = candidateRepository.findAll();
            return candidates.stream()
                    .map(mapper::candidateToCandidateDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerException("Error retrieving candidates", e);
        }
    }

    @Override
    public List<CandidateDTO> getCandidatesByLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            throw new BadRequestException("Language parameter cannot be empty");
        }

        return candidateRepository.findAll().stream()
                .filter(candidate -> candidate.getLanguages() != null &&
                        candidate.getLanguages().stream()
                                .anyMatch(lang -> lang.getLanguage().equalsIgnoreCase(language)))
                .map(candidateMapper::candidateToCandidateDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDTO> getCandidatesBySkill(String skill) {
        try {
            return candidateRepository.findAll().stream()
                    .filter(candidate -> candidate.getSkills() != null &&
                            candidate.getSkills().stream()
                                    .anyMatch(s -> s.getSkillName().equalsIgnoreCase(skill)))
                    .map(candidateMapper::candidateToCandidateDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerException("Error retrieving candidates by skill", e);
        }
    }



    @Override
    public List<String> getAllTechnologies() {
        try {
            Map<String, Long> candidatesBySkill = candidateRepository.findAll().stream()
                    .flatMap(candidate -> candidate.getSkills().stream())
                    .collect(Collectors.groupingBy(Skill::getSkillName, Collectors.counting()));

            List<String> skills = candidatesBySkill.keySet().stream()
                    .sorted()
                    .collect(Collectors.toList());

            if (skills.isEmpty()) {
                throw new ResourceNotFoundException("No technologies found");
            }

            return skills;
        } catch (Exception e) {
            throw new InternalServerException("Error retrieving technologies", e);
        }
    }

    @Override
    public Long getTotalCandidates() {
        try {
            return candidateRepository.count();
        } catch (Exception e) {
            throw new InternalServerException("Error retrieving total candidates count", e);
        }
    }

    @Override
    public Map<String, Long> getCandidatesByLanguage() {
        try {
            return candidateRepository.findAll().stream()
                    .flatMap(candidate -> candidate.getLanguages().stream())
                    .collect(Collectors.groupingBy(Language::getLanguage, Collectors.counting()));
        } catch (Exception e) {
            throw new InternalServerException("Error retrieving candidates by language", e);
        }
    }
}
