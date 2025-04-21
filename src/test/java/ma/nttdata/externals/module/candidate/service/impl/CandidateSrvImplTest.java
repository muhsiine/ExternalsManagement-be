package ma.nttdata.externals.module.candidate.service.impl;

import jakarta.persistence.EntityNotFoundException;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
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
    private CandidateMapper candidateMapper;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CandidateSrvImpl candidateSrv;

    private Candidate candidate;
    private CandidateDTO candidateDTO;
    private UUID candidateId;

    @BeforeEach
    void setUp() {
        candidateId = UUID.randomUUID();

        // Create a candidate entity
        candidate = new Candidate();
        candidate.setId(candidateId);
        candidate.setFullName("John Doe");
        candidate.setBirthDate(LocalDate.of(1990, 1, 1));
        candidate.setYearsOfExperience(5);
        candidate.setGender(GenderEnum.M);
        candidate.setMainTech("Java");
        candidate.setSummary("Experienced Java developer");

        // Create address with country and city
        Address address = new Address();
        Country country = new Country();
        country.setEnglishName("United States");
        City city = new City();
        city.setName("New York");
        city.setCountry(country);
        address.setCountry(country);
        address.setCity(city);
        address.setCandidate(candidate);
        candidate.setAddress(address);

        // Create skills
        List<Skill> skills = new ArrayList<>();
        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setSkillName("Spring Boot");
        skill.setCandidate(candidate);
        skills.add(skill);
        candidate.setSkills(skills);

        // Create languages
        List<Language> languages = new ArrayList<>();
        Language language = new Language();
        language.setId(UUID.randomUUID());
        language.setLanguage("English");
        language.setCandidate(candidate);
        languages.add(language);
        candidate.setLanguages(languages);

        // Create candidate DTO
        candidateDTO = new CandidateDTO(
                candidateId,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                5,
                GenderEnum.M,
                "Java",
                "Experienced Java developer",
                null, null, null, null, null, null, null
        );
    }

    @Test
    void testSave() {
        // Setup
        when(candidateMapper.candidateDTOToCandidate(any(CandidateDTO.class))).thenReturn(candidate);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);
        when(candidateMapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(candidateDTO);
        when(countryRepository.findByEnglishName(anyString())).thenReturn(Optional.empty());
        when(cityRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Execute
        CandidateDTO result = candidateSrv.save(candidateDTO);

        // Verify
        assertNotNull(result);
        assertEquals(candidateDTO.id(), result.id());
        assertEquals(candidateDTO.fullName(), result.fullName());
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void testUpdate() {
        // Setup
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(candidateMapper.candidateDTOToCandidate(any(CandidateDTO.class))).thenReturn(candidate);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);
        when(candidateMapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(candidateDTO);

        // Execute
        CandidateDTO result = candidateSrv.update(candidateId, candidateDTO);

        // Verify
        assertNotNull(result);
        assertEquals(candidateDTO.id(), result.id());
        assertEquals(candidateDTO.fullName(), result.fullName());
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void testUpdateCandidateNotFound() {
        // Setup
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(RuntimeException.class, () -> candidateSrv.update(candidateId, candidateDTO));
        verify(candidateRepository, never()).save(any(Candidate.class));
    }

    @Test
    void testGetAllCandidates() {
        // Setup
        List<Candidate> candidates = Collections.singletonList(candidate);
        when(candidateRepository.findAll()).thenReturn(candidates);
        when(candidateMapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(candidateDTO);

        // Execute
        List<CandidateDTO> result = candidateSrv.getAllCandidates();

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(candidateDTO.id(), result.get(0).id());
        assertEquals(candidateDTO.fullName(), result.get(0).fullName());
    }

    @Test
    void testGetById() {
        // Setup
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(candidateMapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(candidateDTO);

        // Execute
        CandidateDTO result = candidateSrv.getById(candidateId);

        // Verify
        assertNotNull(result);
        assertEquals(candidateDTO.id(), result.id());
        assertEquals(candidateDTO.fullName(), result.fullName());
    }

    @Test
    void testGetByIdCandidateNotFound() {
        // Setup
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(EntityNotFoundException.class, () -> candidateSrv.getById(candidateId));
    }

    @Test
    void testDelete() {
        // Setup
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        // Execute
        boolean result = candidateSrv.delete(candidateId);

        // Verify
        assertTrue(result);
        verify(candidateRepository).deleteById(candidateId);
    }

    @Test
    void testDeleteCandidateNotFound() {
        // Setup
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(RuntimeException.class, () -> candidateSrv.delete(candidateId));
        verify(candidateRepository, never()).deleteById(any());
    }

    @Test
    void testGetCandidates() {
        // Setup
        List<Candidate> candidates = Collections.singletonList(candidate);
        when(candidateRepository.findAll()).thenReturn(candidates);
        when(candidateMapper.candidateToCandidateDTO(any(Candidate.class))).thenReturn(candidateDTO);

        // Execute
        List<CandidateDTO> result = candidateSrv.getCandidates();

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(candidateDTO.id(), result.get(0).id());
        assertEquals(candidateDTO.fullName(), result.get(0).fullName());
    }

    @Test
    void testGetCandidatesByLanguage() {
        // Setup
        List<Candidate> candidates = Collections.singletonList(candidate);
        when(candidateRepository.findAll()).thenReturn(candidates);

        // Execute
        Map<String, Long> result = candidateSrv.getCandidatesByLanguage();

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("English"));
        assertEquals(1L, result.get("English"));
    }

    @Test
    void testGetCandidatesBySkill() {
        // Setup
        List<Candidate> candidates = Collections.singletonList(candidate);
        when(candidateRepository.findAll()).thenReturn(candidates);

        // Execute
        Map<String, Long> result = candidateSrv.getCandidatesBySkill();

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Spring Boot"));
        assertEquals(1L, result.get("Spring Boot"));
    }

    @Test
    void testGetTotalCandidates() {
        // Setup
        when(candidateRepository.count()).thenReturn(5L);

        // Execute
        Long result = candidateSrv.getTotalCandidates();

        // Verify
        assertEquals(5L, result);
    }
}