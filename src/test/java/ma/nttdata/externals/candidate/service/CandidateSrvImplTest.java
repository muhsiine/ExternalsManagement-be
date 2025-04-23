package ma.nttdata.externals.candidate.service;

import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import ma.nttdata.externals.module.candidate.service.impl.CandidateSrvImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateSrvImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CandidateMapper mapper;

    @InjectMocks
    private CandidateSrvImpl candidateSrv;

    private CandidateDTO testCandidateDTO;
    private Candidate testCandidate;
    private Country testCountry;
    private City testCity;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testCountry = new Country();
        testCountry.setId(UUID.randomUUID());
        testCountry.setName("Test Country");
        testCountry.setEnglishName("Test Country");

        testCity = new City();
        testCity.setId(UUID.randomUUID());
        testCity.setName("Test City");
        testCity.setCountry(testCountry);

        Address address = new Address();
        address.setStreet("Test Street");
        address.setPostalCode("12345");
        address.setFullAddress("Test Street, Test City");
        address.setCity(testCity);
        address.setCountry(testCountry);

        testCandidate = new Candidate();
        testCandidate.setId(UUID.randomUUID());
        testCandidate.setFullName("Test Candidate");
        testCandidate.setBirthDate(LocalDate.now());
        testCandidate.setYearsOfExperience(5);
        testCandidate.setGender(GenderEnum.M);
        testCandidate.setMainTech("Java");
        testCandidate.setSummary("Test Summary");
        testCandidate.setAddress(address);

        testCandidateDTO = new CandidateDTO(
                testCandidate.getId(),
                testCandidate.getFullName(),
                testCandidate.getBirthDate(),
                testCandidate.getYearsOfExperience(),
                testCandidate.getGender(),
                testCandidate.getMainTech(),
                testCandidate.getSummary(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                Collections.emptyList());
    }

    @Test
    void save_ShouldSaveCandidate() {
        // Arrange
        when(mapper.candidateDTOToCandidate(any(CandidateDTO.class))).thenReturn(testCandidate);
        when(countryRepository.findByEnglishName(anyString())).thenReturn(Optional.of(testCountry));
        when(cityRepository.findByName(anyString())).thenReturn(Optional.of(testCity));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(testCandidate);
        when(mapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(testCandidateDTO);

        // Act
        CandidateDTO result = candidateSrv.save(testCandidateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testCandidateDTO.id(), result.id());
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void update_ShouldUpdateCandidate() {
        // Arrange
        when(candidateRepository.findById(any(UUID.class))).thenReturn(Optional.of(testCandidate));
        when(mapper.candidateDTOToCandidate(any(CandidateDTO.class))).thenReturn(testCandidate);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(testCandidate);
        when(mapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(testCandidateDTO);

        // Act
        CandidateDTO result = candidateSrv.update(testCandidate.getId(), testCandidateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testCandidateDTO.id(), result.id());
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenCandidateNotFound() {
        // Arrange
        when(candidateRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> candidateSrv.update(testCandidate.getId(), testCandidateDTO));
    }

    @Test
    void getAllCandidates_ShouldReturnAllCandidates() {
        // Arrange
        List<Candidate> candidates = Collections.singletonList(testCandidate);
        when(candidateRepository.findAll()).thenReturn(candidates);
        when(mapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(testCandidateDTO);

        // Act
        List<CandidateDTO> result = candidateSrv.getAllCandidates();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getById_ShouldReturnCandidate() {
        // Arrange
        when(candidateRepository.findById(any(UUID.class))).thenReturn(Optional.of(testCandidate));
        when(mapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(testCandidateDTO);

        // Act
        CandidateDTO result = candidateSrv.getById(testCandidate.getId());

        // Assert
        assertNotNull(result);
        assertEquals(testCandidateDTO.id(), result.id());
    }

    @Test
    void getById_ShouldThrowExceptionWhenCandidateNotFound() {
        // Arrange
        when(candidateRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> candidateSrv.getById(testCandidate.getId()));
    }

    @Test
    void delete_ShouldDeleteCandidate() {
        // Arrange
        when(candidateRepository.findById(any(UUID.class))).thenReturn(Optional.of(testCandidate));
        doNothing().when(candidateRepository).deleteById(any(UUID.class));

        // Act
        boolean result = candidateSrv.delete(testCandidate.getId());

        // Assert
        assertTrue(result);
        verify(candidateRepository).deleteById(any(UUID.class));
    }

    @Test
    void delete_ShouldThrowExceptionWhenCandidateNotFound() {
        // Arrange
        when(candidateRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> candidateSrv.delete(testCandidate.getId()));
    }

    // @Test
    // void getCandidatesByLanguage_ShouldReturnLanguageCounts() {
    // // Arrange
    // List<Candidate> candidates = Collections.singletonList(testCandidate);
    // when(candidateRepository.findAll()).thenReturn(candidates);
    //
    // // Act
    // Map<String, Long> result = candidateSrv.getCandidatesByLanguage();
    //
    // // Assert
    // assertNotNull(result);
    // }

    // @Test
    // void getCandidatesBySkill_ShouldReturnSkillCounts() {
    // // Arrange
    // List<Candidate> candidates = Collections.singletonList(testCandidate);
    // when(candidateRepository.findAll()).thenReturn(candidates);
    //
    // // Act
    // Map<String, Long> result = candidateSrv.getCandidatesBySkill();
    //
    // // Assert
    // assertNotNull(result);
    // }

    @Test
    void getTotalCandidates_ShouldReturnTotalCount() {
        // Arrange
        when(candidateRepository.count()).thenReturn(1L);

        // Act
        Long result = candidateSrv.getTotalCandidates();

        // Assert
        assertEquals(1L, result);
    }
}