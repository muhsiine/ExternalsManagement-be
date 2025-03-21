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

import java.util.Optional;

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

}
