package ma.nttdata.externals.module.candidate.service.impl;

import jakarta.transaction.Transactional;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.City;
import ma.nttdata.externals.module.candidate.entity.Country;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import ma.nttdata.externals.module.candidate.entity.Address;
import org.springframework.stereotype.Service;

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
    @Transactional
    public CandidateDTO update(UUID id, CandidateDTO candidateDTO) {
        Optional<Candidate> existingCandidateOpt = candidateRepository.findById(id);
        if (existingCandidateOpt.isEmpty()) {
            return null;
        }
        Candidate existingCandidate = existingCandidateOpt.get();
        Candidate updatedCandidate = mapper.candidateDTOToCandidate(candidateDTO);

        existingCandidate.setFullName(updatedCandidate.getFullName());
        existingCandidate.setBirthDate(updatedCandidate.getBirthDate());
        existingCandidate.setYearsOfExperience(updatedCandidate.getYearsOfExperience());
        existingCandidate.setGender(updatedCandidate.getGender());
        existingCandidate.setMainTech(updatedCandidate.getMainTech());
        existingCandidate.setSummary(updatedCandidate.getSummary());

        Address updatedAddress = updatedCandidate.getAddress();
        if (updatedAddress != null) {
            Address existingAddress = existingCandidate.getAddress();
            if (existingAddress == null) {
                existingAddress = new Address();
                existingCandidate.setAddress(existingAddress);
            }
            existingAddress.setStreet(updatedAddress.getStreet());
            Country updatedCountry = updatedAddress.getCountry();
            if (updatedCountry != null) {
                Optional<Country> existingCountry = countryRepository.findByEnglishName(updatedCountry.getEnglishName());
                if (existingCountry.isPresent()) {
                    existingAddress.setCountry(existingCountry.get());
                } else {
                    Country savedCountry = countryRepository.save(updatedCountry);
                    existingAddress.setCountry(savedCountry);
                }
            }
            City updatedCity = updatedAddress.getCity();
            if (updatedCity != null) {
                Optional<City> existingCity = cityRepository.findByName(updatedCity.getName());
                if (existingCity.isPresent()) {
                    existingAddress.setCity(existingCity.get());
                } else {
                    City savedCity = cityRepository.save(updatedCity);
                    existingAddress.setCity(savedCity);
                }
            }
        }
        if (updatedCandidate.getContacts() != null) {
            existingCandidate.getContacts().clear();
            existingCandidate.getContacts().addAll(updatedCandidate.getContacts());
            updatedCandidate.getContacts().forEach(contact -> contact.setCandidate(existingCandidate));
        }
        if (updatedCandidate.getExperiences() != null) {
            existingCandidate.getExperiences().clear();
            existingCandidate.getExperiences().addAll(updatedCandidate.getExperiences());
            updatedCandidate.getExperiences().forEach(exp -> exp.setCandidate(existingCandidate));
        }
        if (updatedCandidate.getSkills() != null) {
            existingCandidate.getSkills().clear();
            existingCandidate.getSkills().addAll(updatedCandidate.getSkills());
            updatedCandidate.getSkills().forEach(skill -> skill.setCandidate(existingCandidate));
        }
        if (updatedCandidate.getEducations() != null) {
            existingCandidate.getEducations().clear();
            existingCandidate.getEducations().addAll(updatedCandidate.getEducations());
            updatedCandidate.getEducations().forEach(edu -> edu.setCandidate(existingCandidate));
        }
        if (updatedCandidate.getLanguages() != null) {
            existingCandidate.getLanguages().clear();
            existingCandidate.getLanguages().addAll(updatedCandidate.getLanguages());
            updatedCandidate.getLanguages().forEach(lang -> lang.setCandidate(existingCandidate));
        }

        Candidate savedCandidate = candidateRepository.save(existingCandidate);
        return mapper.candidateToCandidateDTO(savedCandidate);
    }

    @Override
    public List<CandidateDTO> getAllCandidates(){
        List<Candidate> candidates = candidateRepository.findAll();
        return candidates.stream()
                .map(mapper::candidateToCandidateDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateDTO getById(UUID id){
        Optional<Candidate> candidate = candidateRepository.findById(id);
        return candidate.map(mapper::candidateToCandidateDTO).orElse(null);
    }


    @Override
    public boolean delete(UUID id){
        candidateRepository.deleteById(id);
        return true;
    }
}
