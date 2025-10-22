package ma.nttdata.externals.module.candidate.service.impl;

import jakarta.persistence.EntityNotFoundException;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.dto.OfferPassedCandidatesDTO;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.candidate.mapper.CandidateMapper;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import ma.nttdata.externals.module.candidate.repository.LanguageRepository;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.entity.Interview;
import ma.nttdata.externals.module.interview.mapper.EvaluationMapper;
import ma.nttdata.externals.module.interview.mapper.InterviewMapper;
import ma.nttdata.externals.module.interview.service.impl.EvaluationServImpl;
import ma.nttdata.externals.module.interview.service.impl.EvaluationTypeServImpl;
import ma.nttdata.externals.module.interview.service.impl.InterviewServImpl;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.service.impl.OfferServImpl;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateSrvImplTest {

    @Mock
    private CandidateMapper candidateMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private OfferServImpl offerServ;

    @Mock
    private InterviewServImpl interviewServ;

    @Mock
    private EvaluationServImpl evaluationServ;

    @Mock
    private EvaluationTypeServImpl evaluationTypeServ;

    // Don't use @InjectMocks - we'll manually inject
    private CandidateSrvImpl candidateSrv;

    private Candidate candidate;
    private CandidateDTO candidateDTO;
    private UUID candidateId;

    @BeforeEach
    void setUp() {
        // Manually create the instance - adjust constructor parameters based on CandidateSrvImpl
        // This ensures all mocks are properly injected
        candidateSrv = new CandidateSrvImpl(
                candidateMapper,
                candidateRepository,
                countryRepository,
                cityRepository,
                languageRepository,
                offerServ,
                interviewServ,
                evaluationServ,
                evaluationTypeServ,
                evaluationMapper
        );

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
                null, null, null, null, null, null, null,
                Collections.emptyList()
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
        assertEquals(candidateDTO.id(), result.getFirst().id());
        assertEquals(candidateDTO.fullName(), result.getFirst().fullName());
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
        assertEquals(candidateDTO.id(), result.getFirst().id());
        assertEquals(candidateDTO.fullName(), result.getFirst().fullName());
    }

    @Test
    void testGetCandidatesByLanguage() {
        // Setup
        List<Object[]> languageResults = new ArrayList<>();
        Object[] englishResult = new Object[] {"English", 1L};
        languageResults.add(englishResult);

        when(languageRepository.countCandidatesByLanguage()).thenReturn(languageResults);

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

    @Test
    void testGetPassedCandidatesForOffer() {
        // Debug: verify mocks are not null
        assertNotNull(offerServ, "offerServ mock should not be null");
        assertNotNull(interviewServ, "interviewServ mock should not be null");
        assertNotNull(evaluationServ, "evaluationServ mock should not be null");

        // Setup - use the candidateId from setUp()
        UUID offerId = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        UUID evaluationTypeId = UUID.randomUUID();

        // Mock OfferDTO - correct constructor with 5 parameters
        OfferDTO offerDTO = new OfferDTO(
                offerId,
                "Test Offer",
                "Test Description",
                null,
                Collections.emptyList()
        );
        when(offerServ.getOfferById(offerId)).thenReturn(offerDTO);

        // Mock Interview - IMPORTANT: use candidateId from setUp()
        InterviewDTO interview = new InterviewDTO(
                interviewId, null, null, null, null, null,
                null, 0, 0, offerId, candidateId, new ArrayList<>(),
                new ArrayList<>(),""
        );
        when(interviewServ.getInterviewsByOfferId(offerId))
                .thenReturn(Collections.singletonList(interview));

        // Mock the repository to return the candidate entity
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(candidateMapper.candidateToCandidateDTO(candidate)).thenReturn(candidateDTO);

        // Mock Evaluation entity
        EvaluationType evaluationType = new EvaluationType();
        evaluationType.setId(evaluationTypeId);
        evaluationType.setDescription("Technical");

        Evaluation evaluation = new Evaluation();
        evaluation.setId(UUID.randomUUID());
        evaluation.setScore(95.0);
        evaluation.setFeedback("Excellent");
        Interview mockInterview = new Interview();
        mockInterview.setId(interviewId);
        evaluation.setInterview(mockInterview);
        evaluation.setEvaluationType(evaluationType);

        when(evaluationServ.getAllEvaluationsByInterviewID(interviewId))
                .thenReturn(Collections.singletonList(evaluation));

        // Mock mapper
        EvaluationDTO evaluationDTO = new EvaluationDTO(
                evaluation.getId(),
                evaluation.getScore(),
                evaluation.getFeedback(),
                interviewId,
                evaluationTypeId
        );
        when(evaluationMapper.toDto(evaluation)).thenReturn(evaluationDTO);

        // Mock EvaluationType service
        EvaluationTypeDTO evaluationTypeDTO = new EvaluationTypeDTO(evaluationTypeId, "Technical", 3.0);
        when(evaluationTypeServ.getTypeById(evaluationTypeId)).thenReturn(evaluationTypeDTO);

        // Execute
        List<OfferPassedCandidatesDTO> result = candidateSrv.getPassedCandidatesForOffer(offerId);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());

        OfferPassedCandidatesDTO candidateResult = result.get(0);
        assertEquals(candidateId, candidateResult.id());
        assertEquals(candidateDTO.fullName(), candidateResult.fullName());
        assertEquals(1, candidateResult.evaluations().size());
        assertEquals("Technical", candidateResult.evaluations().get(0).evaluationType().description());

        // Verify mocks were called
        verify(offerServ).getOfferById(offerId);
        verify(interviewServ).getInterviewsByOfferId(offerId);
        verify(candidateRepository).findById(candidateId);
        verify(evaluationServ).getAllEvaluationsByInterviewID(interviewId);
        verify(evaluationMapper).toDto(evaluation);
        verify(evaluationTypeServ).getTypeById(evaluationTypeId);
    }
}