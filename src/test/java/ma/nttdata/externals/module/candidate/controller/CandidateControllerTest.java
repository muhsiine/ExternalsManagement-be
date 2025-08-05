package ma.nttdata.externals.module.candidate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.*;
import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CandidateController.class)
class CandidateControllerTest {

    public static final String API_URL = "/api/v1/candidates";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CandidateSrv candidateSrv;

    private CandidateDTO candidateDTO;
    private UUID candidateId;
    private List<CandidateDTO> candidateList;
    private InterviewDTO interviewDTO;

    @BeforeEach
    void setUp() {
        candidateId = UUID.randomUUID();
        UUID interviewId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        // Create an InterviewDTO to include in candidateDTO
        interviewDTO = new InterviewDTO(
                interviewId,
                LocalDateTime.of(2025, 7, 21, 9, 0),
                LocalDateTime.of(2025, 7, 21, 10, 0),
                "Technical round",
                "https://zoom.com/meeting",
                "Very good performance",
                LocalDateTime.of(2025, 8, 3, 6, 0),
                "Candidate showed great problem-solving skills",
                1,
                2,
                candidateId,
                offerId,
                Collections.emptyList(), // evaluations
                Collections.emptyList() // questions
        );

        // Create a candidate DTO with the new structure
        candidateDTO = new CandidateDTO(
                candidateId,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                5,
                GenderEnum.M,
                "Java",
                "Experienced Java developer",
                Collections.emptyList(), // contacts
                Collections.emptyList(), // experiences
                Collections.singletonList(new SkillDTO(UUID.randomUUID(), "Java", null)), // skills
                Collections.emptyList(), // educations
                Collections.emptyList(), // cvFiles
                null, // address
                Collections.singletonList(new LanguageDTO(UUID.randomUUID(), null, "English", "Fluent in English", null, "English", "Fluent", null, false)), // naturalLanguages
                Collections.singletonList(interviewDTO) // interviews
        );

        // Create a list of candidates
        candidateList = new ArrayList<>();
        candidateList.add(candidateDTO);
        candidateList.add(new CandidateDTO(
                UUID.randomUUID(),
                "Jane Smith",
                LocalDate.of(1985, 5, 15),
                8,
                GenderEnum.F,
                "Python",
                "Skilled Python developer",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.singletonList(new SkillDTO(UUID.randomUUID(), "Python", null)),
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                Collections.singletonList(new LanguageDTO(UUID.randomUUID(), null, "French", "Fluent in French", null, "French", "Fluent", null, false)),
                Collections.emptyList()
        ));
    }

    @Test
    @WithMockUser
    void testCreateCandidate() throws Exception {
        // Arrange
        CandidateDTO inputDto = new CandidateDTO(
                null,
                candidateDTO.fullName(),
                candidateDTO.birthDate(),
                candidateDTO.yearsOfExperience(),
                candidateDTO.gender(),
                candidateDTO.mainTech(),
                candidateDTO.summary(),
                candidateDTO.contacts(),
                candidateDTO.experiences(),
                candidateDTO.skills(),
                candidateDTO.educations(),
                candidateDTO.cvFiles(),
                candidateDTO.address(),
                candidateDTO.naturalLanguages(),
                candidateDTO.interviews()
        );

        when(candidateSrv.save(any(CandidateDTO.class))).thenReturn(candidateDTO);

        // Act & Assert
        mockMvc.perform(post(API_URL)
                        .contentType("application/json")
                        .accept("application/json")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.mainTech").value("Java"))
                .andExpect(jsonPath("$.yearsOfExperience").value(5))
                .andExpect(jsonPath("$.summary").value("Experienced Java developer"))
                .andExpect(jsonPath("$.interviews[0].id").value(interviewDTO.id().toString()))
                .andExpect(jsonPath("$.skills[0].skillName").value("Java")) // Fixed from .skill to .name
                .andExpect(jsonPath("$.naturalLanguages[0].language").value("English"));

        verify(candidateSrv).save(any(CandidateDTO.class));
    }

    @Test
    @WithMockUser
    void testUpdateCandidate() throws Exception {
        // Arrange
        CandidateDTO updatedDto = new CandidateDTO(
                candidateId,
                "John Doe Updated",
                candidateDTO.birthDate(),
                6,
                candidateDTO.gender(),
                candidateDTO.mainTech(),
                "Updated Java developer profile",
                candidateDTO.contacts(),
                candidateDTO.experiences(),
                candidateDTO.skills(),
                candidateDTO.educations(),
                candidateDTO.cvFiles(),
                candidateDTO.address(),
                candidateDTO.naturalLanguages(),
                candidateDTO.interviews()
        );

        when(candidateSrv.update(eq(candidateId), any(CandidateDTO.class))).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put(API_URL + "/{id}", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe Updated"))
                .andExpect(jsonPath("$.mainTech").value("Java"))
                .andExpect(jsonPath("$.yearsOfExperience").value(6))
                .andExpect(jsonPath("$.summary").value("Updated Java developer profile"))
                .andExpect(jsonPath("$.interviews[0].id").value(interviewDTO.id().toString()))
                .andExpect(jsonPath("$.skills[0].skillName").value("Java")) // Fixed from .skill to .name
                .andExpect(jsonPath("$.naturalLanguages[0].language").value("English"));

        verify(candidateSrv).update(eq(candidateId), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser
    void testUpdateCandidateNotFound() throws Exception {
        // Arrange
        when(candidateSrv.update(eq(candidateId), any(CandidateDTO.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put(API_URL + "/{id}", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(candidateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());

        verify(candidateSrv).update(eq(candidateId), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser
    void testPatchCandidate() throws Exception {
        // Arrange
        CandidateDTO patchedDto = new CandidateDTO(
                candidateId,
                "John Doe Patched",
                candidateDTO.birthDate(),
                5,
                candidateDTO.gender(),
                candidateDTO.mainTech(),
                candidateDTO.summary(),
                candidateDTO.contacts(),
                candidateDTO.experiences(),
                candidateDTO.skills(),
                candidateDTO.educations(),
                candidateDTO.cvFiles(),
                candidateDTO.address(),
                candidateDTO.naturalLanguages(),
                candidateDTO.interviews()
        );

        when(candidateSrv.update(eq(candidateId), any(CandidateDTO.class))).thenReturn(patchedDto);

        // Act & Assert
        mockMvc.perform(patch(API_URL + "/{id}", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(patchedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe Patched"))
                .andExpect(jsonPath("$.mainTech").value("Java"))
                .andExpect(jsonPath("$.yearsOfExperience").value(5))
                .andExpect(jsonPath("$.summary").value("Experienced Java developer"))
                .andExpect(jsonPath("$.interviews[0].id").value(interviewDTO.id().toString()))
                .andExpect(jsonPath("$.skills[0].skillName").value("Java")) // Fixed from .skill to .name
                .andExpect(jsonPath("$.naturalLanguages[0].language").value("English"));

        verify(candidateSrv).update(eq(candidateId), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser
    void testPatchCandidateNotFound() throws Exception {
        // Arrange
        when(candidateSrv.update(eq(candidateId), any(CandidateDTO.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(patch(API_URL + "/{id}", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(candidateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());

        verify(candidateSrv).update(eq(candidateId), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser
    void testGetAllCandidates() throws Exception {
        // Arrange
        when(candidateSrv.getAllCandidates()).thenReturn(candidateList);

        // Act & Assert
        mockMvc.perform(get(API_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(candidateId.toString()))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].mainTech").value("Java"))
                .andExpect(jsonPath("$[0].interviews[0].id").value(interviewDTO.id().toString()))
                .andExpect(jsonPath("$[1].fullName").value("Jane Smith"))
                .andExpect(jsonPath("$[1].mainTech").value("Python"));

        verify(candidateSrv).getAllCandidates();
    }

    @Test
    @WithMockUser
    void testGetAllCandidatesEmpty() throws Exception {
        // Arrange
        when(candidateSrv.getAllCandidates()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(API_URL))
                .andExpect(status().isNotFound());

        verify(candidateSrv).getAllCandidates();
    }

    @Test
    @WithMockUser
    void testGetCandidateById() throws Exception {
        // Arrange
        when(candidateSrv.getById(candidateId)).thenReturn(candidateDTO);

        // Act & Assert
        mockMvc.perform(get(API_URL + "/{id}", candidateId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.mainTech").value("Java"))
                .andExpect(jsonPath("$.yearsOfExperience").value(5))
                .andExpect(jsonPath("$.summary").value("Experienced Java developer"))
                .andExpect(jsonPath("$.interviews[0].id").value(interviewDTO.id().toString()))
                .andExpect(jsonPath("$.skills[0].skillName").value("Java")) // Fixed from .skill to .name
                .andExpect(jsonPath("$.naturalLanguages[0].language").value("English"));

        verify(candidateSrv).getById(candidateId);
    }

    @Test
    @WithMockUser
    void testGetCandidateByIdNotFound() throws Exception {
        // Arrange
        when(candidateSrv.getById(candidateId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get(API_URL + "/{id}", candidateId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());

        verify(candidateSrv).getById(candidateId);
    }

    @Test
    @WithMockUser
    void testDeleteCandidate() throws Exception {
        // Arrange
        when(candidateSrv.delete(candidateId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete(API_URL + "/{id}", candidateId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Candidate deleted successfully"));

        verify(candidateSrv).delete(candidateId);
    }

    @Test
    @WithMockUser
    void testDeleteCandidateNotFound() throws Exception {
        // Arrange
        when(candidateSrv.delete(candidateId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete(API_URL + "/{id}", candidateId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());

        verify(candidateSrv).delete(candidateId);
    }

    @Test
    @WithMockUser
    void testGetAllTechnologies() throws Exception {
        // Arrange
        Map<String, Long> skillsMap = new HashMap<>();
        skillsMap.put("Java", 5L);
        skillsMap.put("Python", 3L);
        skillsMap.put("Spring", 4L);

        when(candidateSrv.getCandidatesBySkill()).thenReturn(skillsMap);

        // Act & Assert
        mockMvc.perform(get(API_URL + "/technologies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", containsInAnyOrder("Java", "Python", "Spring")));

        verify(candidateSrv).getCandidatesBySkill();
    }

    @Test
    @WithMockUser
    void testGetCandidatesByLanguage() throws Exception {
        // Arrange
        List<CandidateDTO> candidatesWithEnglish = Collections.singletonList(candidateDTO);

        doReturn(candidatesWithEnglish).when(candidateSrv).getCandidates();

        // Act & Assert
        mockMvc.perform(get(API_URL + "/languages/{lang}", "English")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].naturalLanguages[0].language").value("English"));

        verify(candidateSrv).getCandidates();
    }

    @Test
    @WithMockUser
    void testGetCandidatesBySkill() throws Exception {
        // Arrange
        List<CandidateDTO> candidatesWithJava = Collections.singletonList(candidateDTO);

        doReturn(candidatesWithJava).when(candidateSrv).getCandidates();

        // Act & Assert
        mockMvc.perform(get(API_URL + "/skills/{skill}", "Java")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"));

        verify(candidateSrv).getCandidates();
    }
}