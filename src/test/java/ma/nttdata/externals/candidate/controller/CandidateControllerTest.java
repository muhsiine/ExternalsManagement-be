package ma.nttdata.externals.candidate.controller;

import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.controller.CandidateController;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateControllerTest {

    @Mock
    private CandidateSrv candidateSrv;

    @InjectMocks
    private CandidateController candidateController;

    private CandidateDTO testCandidateDTO;

    @BeforeEach
    void setUp() {
        testCandidateDTO = new CandidateDTO(
                UUID.randomUUID(),
                "Test Candidate",
                LocalDate.now(),
                5,
                GenderEnum.M,
                "Java",
                "Test Summary",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                Collections.emptyList());
    }

    @Test
    void createCandidate_ShouldReturnCreatedResponse() {
        // Arrange
        when(candidateSrv.save(any(CandidateDTO.class))).thenReturn(testCandidateDTO);

        // Act
        ResponseEntity<?> response = candidateController.candidate(testCandidateDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testCandidateDTO, response.getBody());
    }

    @Test
    void updateCandidate_ShouldReturnOkResponse() {
        // Arrange
        when(candidateSrv.update(any(UUID.class), any(CandidateDTO.class))).thenReturn(testCandidateDTO);

        // Act
        ResponseEntity<?> response = candidateController.updateCandidate(testCandidateDTO.id(), testCandidateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCandidateDTO, response.getBody());
    }

    @Test
    void updateCandidate_ShouldReturnNotFoundResponse() {
        // Arrange
        when(candidateSrv.update(any(UUID.class), any(CandidateDTO.class))).thenReturn(null);

        // Act
        ResponseEntity<?> response = candidateController.updateCandidate(testCandidateDTO.id(), testCandidateDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidate not found", response.getBody());
    }

    @Test
    void patchCandidate_ShouldReturnOkResponse() {
        // Arrange
        when(candidateSrv.update(any(UUID.class), any(CandidateDTO.class))).thenReturn(testCandidateDTO);

        // Act
        ResponseEntity<?> response = candidateController.patchCandidate(testCandidateDTO.id(), testCandidateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCandidateDTO, response.getBody());
    }

    @Test
    void patchCandidate_ShouldReturnNotFoundResponse() {
        // Arrange
        when(candidateSrv.update(any(UUID.class), any(CandidateDTO.class))).thenReturn(null);

        // Act
        ResponseEntity<?> response = candidateController.patchCandidate(testCandidateDTO.id(), testCandidateDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidate not found", response.getBody());
    }

    @Test
    void getAllCandidates_ShouldReturnOkResponse() {
        // Arrange
        List<CandidateDTO> candidates = Collections.singletonList(testCandidateDTO);
        when(candidateSrv.getAllCandidates()).thenReturn(candidates);

        // Act
        ResponseEntity<List<CandidateDTO>> response = candidateController.getAllCandidates();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidates, response.getBody());
    }

    @Test
    void getAllCandidates_ShouldReturnNotFoundResponse() {
        // Arrange
        when(candidateSrv.getAllCandidates()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<CandidateDTO>> response = candidateController.getAllCandidates();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCandidate_ShouldReturnOkResponse() {
        // Arrange
        when(candidateSrv.getById(any(UUID.class))).thenReturn(testCandidateDTO);

        // Act
        ResponseEntity<?> response = candidateController.getCandidate(testCandidateDTO.id());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCandidateDTO, response.getBody());
    }

    @Test
    void getCandidate_ShouldReturnNotFoundResponse() {
        // Arrange
        when(candidateSrv.getById(any(UUID.class))).thenReturn(null);

        // Act
        ResponseEntity<?> response = candidateController.getCandidate(testCandidateDTO.id());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidate not found", response.getBody());
    }

    @Test
    void deleteCandidate_ShouldReturnOkResponse() {
        // Arrange
        when(candidateSrv.delete(any(UUID.class))).thenReturn(true);

        // Act
        ResponseEntity<?> response = candidateController.deleteCandidate(testCandidateDTO.id());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Candidate deleted successfully", response.getBody());
    }

    @Test
    void deleteCandidate_ShouldReturnNotFoundResponse() {
        // Arrange
        when(candidateSrv.delete(any(UUID.class))).thenReturn(false);

        // Act
        ResponseEntity<?> response = candidateController.deleteCandidate(testCandidateDTO.id());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidate not found", response.getBody());
    }

    @Test
    void getAllTechnologies_ShouldReturnOkResponse() {
        // Arrange
        Map<String, Long> skills = new HashMap<>();
        skills.put("Java", 1L);
        when(candidateSrv.getCandidatesBySkill()).thenReturn(skills);

        // Act
        ResponseEntity<List<String>> response = candidateController.getAllTechnologies();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Java", response.getBody().get(0));
    }

    @Test
    void getCandidatesByLanguage_ShouldReturnOkResponse() {
        // Arrange
        List<CandidateDTO> candidates = Collections.singletonList(testCandidateDTO);
        when(candidateSrv.getCandidates()).thenReturn(candidates);

        // Act
        ResponseEntity<List<CandidateDTO>> response = candidateController.getCandidatesByLanguage("English");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getCandidatesBySkill_ShouldReturnOkResponse() {
        // Arrange
        List<CandidateDTO> candidates = Collections.singletonList(testCandidateDTO);
        when(candidateSrv.getCandidates()).thenReturn(candidates);

        // Act
        ResponseEntity<List<CandidateDTO>> response = candidateController.getCandidatesBySkill("Java");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}