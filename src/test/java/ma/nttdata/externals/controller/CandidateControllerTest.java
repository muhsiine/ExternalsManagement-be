package ma.nttdata.externals.controller;

import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.constants.LanguageLevel;
import ma.nttdata.externals.module.candidate.constants.ProficiencyLevel;
import ma.nttdata.externals.module.candidate.controller.CandidateController;
import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import ma.nttdata.externals.module.cv.dto.CvFileDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class CandidateControllerTest {

    @Mock
    private CandidateSrv candidateSrv;

    @InjectMocks
    private CandidateController candidateController;

    private CandidateDTO candidateDTO;
    private UUID testId;
    private CityDTO cityDTO;
    private CountryDTO countryDTO;

    @BeforeEach
    void setUp(){
        testId = UUID.randomUUID();
        candidateDTO = createSampleCandidateDTO();
    }

    private CandidateDTO createSampleCandidateDTO(){
        return new CandidateDTO(
                testId,
                "Ahmed Ahmadi",
                LocalDate.of(2003,3,3),
                3,
                GenderEnum.M,
                "Java",
                "Experienced very much",
                Collections.singletonList(new ContactDTO(UUID.randomUUID(),"email","test@example.com")),
                Collections.singletonList(new ExperienceDTO(UUID.randomUUID(),"Software Engineer","Tech Corp",("2012,12,12") ,("2023,1,1"),"Experience Description")),
                Collections.singletonList(new SkillDTO(UUID.randomUUID(),"Java", ProficiencyLevel.BEGINNER)),
                Collections.singletonList(new EducationDTO(UUID.randomUUID(),"University","Specialised software",("2010,10,10"),("2024,1,1"),"Computer Science")),
                Collections.singletonList(new CvFileDTO("Doc1233","29tZSBiYXNlNjQg","application/pdf",null,"/documents/cv.pdf")),
                new AddressDTO(UUID.randomUUID(),"123 MAin St","12345","Av FAR, Rabat,Maroc",cityDTO,candidateDTO,countryDTO),
                Collections.singletonList(new LanguageDTO(UUID.randomUUID(),candidateDTO ,"Anglais","English","professionalism","Anglais","English", LanguageLevel.ADVANCED,true))

        );
    }

    @Test
    void testCreateCandidate(){
        CandidateDTO candidates = createSampleCandidateDTO();
        when(candidateSrv.save(any(CandidateDTO.class))).thenReturn(candidateDTO);
        ResponseEntity<?> response = candidateController.candidate(candidates);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(candidateDTO, response.getBody());
        verify(candidateSrv).save(candidates);
    }

    @Test
    void testUpdateCandidate(){
        when(candidateSrv.update(eq(testId), any(CandidateDTO.class))).thenReturn(candidateDTO);
        ResponseEntity<?> response = candidateController.updateCandidate(testId, candidateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidateDTO, response.getBody());
        verify(candidateSrv).update(testId, candidateDTO);
    }

    @Test
    void testUpdateCandidate_NotFound(){
        when(candidateSrv.update(eq(testId),any(CandidateDTO.class))).thenReturn(null);
        ResponseEntity<?> response = candidateController.updateCandidate(testId, candidateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidate not found", response.getBody());
        verify(candidateSrv).update(testId, candidateDTO);
    }

    @Test
    void testPatchCandidate(){
        when(candidateSrv.update(eq(testId),any(CandidateDTO.class))).thenReturn(candidateDTO);
        ResponseEntity<?> response = candidateController.patchCandidate(testId, candidateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidateDTO, response.getBody());
        verify(candidateSrv).update(testId,candidateDTO);
    }

    @Test
    void testPatchCandidate_NotFound(){
        when(candidateSrv.update(eq(testId),any(CandidateDTO.class))).thenReturn(null);
        ResponseEntity<?> response = candidateController.patchCandidate(testId,candidateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidate not found", response.getBody());
        verify(candidateSrv).update(testId,candidateDTO);
    }

    @Test
    void testGetAllCandidate(){
        List<CandidateDTO> candidates = Collections.singletonList(candidateDTO);
        when(candidateSrv.getAllCandidates()).thenReturn(candidates);
        ResponseEntity<?> response = candidateController.getAllCandidates();

        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertEquals(candidates, response.getBody());
        verify(candidateSrv).getAllCandidates();
    }

    @Test
    void testGetAllCandidate_NotFound(){
        when(candidateSrv.getAllCandidates()).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = candidateController.getAllCandidates();

        assertEquals(HttpStatus.NOT_FOUND , response.getStatusCode());
        assertNull(response.getBody());
        verify(candidateSrv).getAllCandidates();
    }

    @Test
    void testGetCandidate(){
        when(candidateSrv.getById(testId)).thenReturn(candidateDTO);
        ResponseEntity<?> response = candidateController.getCandidate(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidateDTO, response.getBody());
        verify(candidateSrv).getById(testId);

    }

    @Test
    void testGetCandidate_NotFound(){
        when(candidateSrv.getById(testId)).thenReturn(null);
        ResponseEntity<?> response = candidateController.getCandidate(testId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidate not found", response.getBody());
        verify(candidateSrv).getById(testId);
    }

    @Test
    void testDeleteCandidate(){
        when(candidateSrv.delete(testId)).thenReturn(true);
        ResponseEntity<?> response = candidateController.deleteCandidate(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Candidate deleted successfully", response.getBody());
        verify(candidateSrv).delete(testId);
    }

    @Test
    void testDeleteCandidate_NotFound(){
        when(candidateSrv.delete(testId)).thenReturn(false);
        ResponseEntity<?> response = candidateController.deleteCandidate(testId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Candidate not found", response.getBody());
        verify(candidateSrv).delete(testId);
    }
}
