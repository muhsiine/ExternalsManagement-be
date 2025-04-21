package ma.nttdata.externals.module.candidate.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ma.nttdata.externals.commons.exception.BadRequestException;
import ma.nttdata.externals.commons.exception.InternalServerException;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CandidateSrvImpl implements CandidateSrv {

    private static final Logger logger = LoggerFactory.getLogger(CandidateSrvImpl.class);
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
        try {
            if (candidateDTO == null) {
                logger.warn("Cannot save null candidate");
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
            logger.info("Candidate saved successfully with ID: {}", savedCandidate.getId());
            return mapper.candidateToCandidateDTO(savedCandidate);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating candidate: {}", e.getMessage(), e);
            throw new InternalServerException("Error creating candidate", e);
        }
    }

    @Override
    @Transactional
    public CandidateDTO update(UUID id, CandidateDTO candidateDTO) {
        try {
            if (id == null) {
                logger.warn("Cannot update candidate with null ID");
                throw new BadRequestException("Candidate ID is required");
            }

            if (candidateDTO == null) {
                logger.warn("Cannot update to null candidate data");
                throw new BadRequestException("Candidate data is required");
            }

            Optional<Candidate> existingCandidateOpt = candidateRepository.findById(id);
            if (existingCandidateOpt.isEmpty()) {
                logger.warn("Candidate not found with ID: {}", id);
                throw new ResourceNotFoundException("Candidate", id);
            }

            Candidate updatedCandidate = mapper.candidateDTOToCandidate(candidateDTO);
            Candidate savedCandidate = candidateRepository.save(updatedCandidate);
            logger.info("Candidate updated successfully with ID: {}", id);
            return mapper.candidateToCandidateDTO(savedCandidate);
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating candidate with ID {}: {}", id, e.getMessage(), e);
            throw new InternalServerException("Error updating candidate", e);
        }
    }

    @Override
    public List<CandidateDTO> getAllCandidates() {
        try {
            List<Candidate> candidates = candidateRepository.findAll();

            if (candidates.isEmpty()) {
                logger.info("No candidates found");
                throw new ResourceNotFoundException("No candidates found");
            }

            logger.info("Retrieved {} candidates", candidates.size());
            return candidates.stream()
                    .map(mapper::candidateToCandidateDTO)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving all candidates: {}", e.getMessage(), e);
            throw new InternalServerException("Error retrieving candidates", e);
        }
    }

    @Override
    public CandidateDTO getById(UUID id) {
        try {
            if (id == null) {
                logger.warn("Cannot get candidate with null ID");
                throw new BadRequestException("Candidate ID is required");
            }

            Optional<Candidate> candidate = candidateRepository.findById(id);
            if (candidate.isEmpty()) {
                logger.warn("Candidate not found with ID: {}", id);
                throw new EntityNotFoundException("Candidate id not found : " + id);
            }

            logger.info("Retrieved candidate with ID: {}", id);
            return mapper.candidateToCandidateDTO(candidate.get());
        } catch (EntityNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving candidate with ID {}: {}", id, e.getMessage(), e);
            throw new InternalServerException("Error retrieving candidate", e);
        }
    }


    @Override
    public boolean delete(UUID id) {
        try {
            if (id == null) {
                logger.warn("Cannot delete candidate with null ID");
                throw new BadRequestException("Candidate ID is required");
            }

            Optional<Candidate> candidate = candidateRepository.findById(id);
            if (candidate.isEmpty()) {
                logger.warn("Candidate not found with ID: {}", id);
                throw new ResourceNotFoundException("Candidate", id);
            }

            candidateRepository.deleteById(id);
            logger.info("Candidate deleted successfully with ID: {}", id);
            return true;
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting candidate with ID {}: {}", id, e.getMessage(), e);
            throw new InternalServerException("Error deleting candidate", e);
        }
    }

    @Override
    public List<CandidateDTO> getCandidates() {
        try {
            List<Candidate> candidates = candidateRepository.findAll();
            logger.info("Retrieved {} candidates", candidates.size());
            return candidates.stream()
                    .map(mapper::candidateToCandidateDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving candidates: {}", e.getMessage(), e);
            throw new InternalServerException("Error retrieving candidates", e);
        }
    }

    @Override
    public Map<String, Long> getCandidatesByLanguage() {
        try {
            Map<String, Long> candidatesByLanguage = candidateRepository.findAll().stream()
                    .flatMap(candidate -> candidate.getLanguages().stream())
                    .collect(Collectors.groupingBy(Language::getLanguage, Collectors.counting()));

            logger.info("Retrieved candidates by language count: {}", candidatesByLanguage.size());
            return candidatesByLanguage;
        } catch (Exception e) {
            logger.error("Error retrieving candidates by language: {}", e.getMessage(), e);
            throw new InternalServerException("Error retrieving candidates by language", e);
        }
    }

    @Override
    public Map<String, Long> getCandidatesBySkill() {
        try {
            Map<String, Long> candidatesBySkill = candidateRepository.findAll().stream()
                    .flatMap(candidate -> candidate.getSkills().stream())
                    .collect(Collectors.groupingBy(Skill::getSkillName, Collectors.counting()));

            logger.info("Retrieved candidates by skill count: {}", candidatesBySkill.size());
            return candidatesBySkill;
        } catch (Exception e) {
            logger.error("Error retrieving candidates by skill: {}", e.getMessage(), e);
            throw new InternalServerException("Error retrieving candidates by skill", e);
        }
    }

    @Override
    public Long getTotalCandidates() {
        try {
            Long totalCandidates = candidateRepository.count();
            logger.info("Total candidates count: {}", totalCandidates);
            return totalCandidates;
        } catch (Exception e) {
            logger.error("Error retrieving total candidates count: {}", e.getMessage(), e);
            throw new InternalServerException("Error retrieving total candidates count", e);
        }
    }
}
