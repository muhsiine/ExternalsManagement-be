package ma.nttdata.externals.serviceImpl;

import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.AddressDTO;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.dto.CityDTO;
import ma.nttdata.externals.module.candidate.dto.CountryDTO;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.candidate.entity.City;
import ma.nttdata.externals.module.candidate.entity.Country;
import ma.nttdata.externals.module.candidate.entity.Address;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import ma.nttdata.externals.module.candidate.service.impl.CandidateSrvImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceImplTest {

    @Mock
    private CandidateMapper mapper;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CandidateSrvImpl candidateService;

    private Candidate candidate;
    private CandidateDTO candidateDTO;
    private UUID candidateId;
    private CityDTO cityDTO;
    private CountryDTO countryDTO;

    @BeforeEach
    void setUp() {

        candidateId = UUID.randomUUID();

        Country country = new Country();
        country.setEnglishName("TestCountry");

        City city = new City();
        city.setName("TestCity");
        city.setCountry(country);

        Address address = new Address();
        address.setCountry(country);
        address.setCity(city);

        candidate = new Candidate();
        candidate.setId(candidateId);
        candidate.setAddress(address);

        AddressDTO addressDTO = new AddressDTO(
                null,
                "TestStreet",
                "TestPostalCode",
                "full Address",
                cityDTO,
                null,
                countryDTO
        );

        candidateDTO = new CandidateDTO(
                candidateId,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                5,
                GenderEnum.M,
                "Java",
                "Software Developer",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                addressDTO,
                Collections.emptyList()
        );
    }

    @Test
    void testSave() {
        when(mapper.candidateDTOToCandidate(candidateDTO)).thenReturn(candidate);
        when(countryRepository.findByEnglishName(anyString())).thenReturn(Optional.of(candidate.getAddress().getCountry()));
        when(cityRepository.findByName(anyString())).thenReturn(Optional.of(candidate.getAddress().getCity()));
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(mapper.candidateToCandidateDTO(candidate)).thenReturn(candidateDTO);
        CandidateDTO savedCandidate = candidateService.save(candidateDTO);
        assertNotNull(savedCandidate);
        assertEquals(candidateId, savedCandidate.id());
        verify(mapper).candidateDTOToCandidate(candidateDTO);
        verify(candidateRepository).save(candidate);
    }

    @Test
    void testUpdate() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(mapper.candidateDTOToCandidate(candidateDTO)).thenReturn(candidate);
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(mapper.candidateToCandidateDTO(candidate)).thenReturn(candidateDTO);
        CandidateDTO updatedCandidate = candidateService.update(candidateId, candidateDTO);
        assertNotNull(updatedCandidate);
        assertEquals(candidateId, updatedCandidate.id());
        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository).save(candidate);
    }

    @Test
    void testUpdateCandidateNotFound() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            candidateService.update(candidateId, candidateDTO);
        });
    }

    @Test
    void testGetAllCandidates() {
        List<Candidate> candidates = Arrays.asList(candidate);
        when(candidateRepository.findAll()).thenReturn(candidates);
        when(mapper.candidateToCandidateDTO(candidate)).thenReturn(candidateDTO);
        List<CandidateDTO> candidateDTOs = candidateService.getAllCandidates();
        assertNotNull(candidateDTOs);
        assertEquals(1, candidateDTOs.size());
        verify(candidateRepository).findAll();
    }

    @Test
    void testGetById() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(mapper.candidateToCandidateDTO(candidate)).thenReturn(candidateDTO);
        CandidateDTO foundCandidate = candidateService.getById(candidateId);
        assertNotNull(foundCandidate);
        assertEquals(candidateId, foundCandidate.id());
        verify(candidateRepository).findById(candidateId);
    }

    @Test
    void testGetByIdNotFound() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            candidateService.getById(candidateId);
        });
    }

    @Test
    void testDelete() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        boolean result = candidateService.delete(candidateId);
        assertTrue(result);
        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository).deleteById(candidateId);
    }

    @Test
    void testDeleteCandidateNotFound() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            candidateService.delete(candidateId);
        });
    }
}