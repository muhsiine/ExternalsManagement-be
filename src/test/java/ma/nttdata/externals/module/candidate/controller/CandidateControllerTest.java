package ma.nttdata.externals.module.candidate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.dto.LanguageDTO;
import ma.nttdata.externals.module.candidate.dto.SkillDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CandidateSrv candidateSrv;

    private CandidateDTO candidateDTO;
    private UUID candidateId;
    private List<CandidateDTO> candidateList;

    @BeforeEach
    void setUp() {
        candidateId = UUID.randomUUID();

        // Create a candidate DTO
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
                null, null, null, null, null, null, null
        ));
    }

    @Test
    @WithMockUser
    void testCreateCandidate() throws Exception {
        when(candidateSrv.save(any(CandidateDTO.class))).thenReturn(candidateDTO);

        mockMvc.perform(post("/candidates")
                        .contentType("application/json")
                        .accept("application/json")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(candidateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"));

        verify(candidateSrv).save(any(CandidateDTO.class));
    }

    @Test
    @WithMockUser
    void testUpdateCandidate() throws Exception {
        when(candidateSrv.update(eq(candidateId), any(CandidateDTO.class))).thenReturn(candidateDTO);

        mockMvc.perform(put("/candidates/{id}", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(candidateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"));

        verify(candidateSrv).update(eq(candidateId), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser
    void testUpdateCandidateNotFound() throws Exception {
        when(candidateSrv.update(eq(candidateId), any(CandidateDTO.class))).thenReturn(null);

        mockMvc.perform(put("/candidates/{id}", candidateId)
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
        when(candidateSrv.update(eq(candidateId), any(CandidateDTO.class))).thenReturn(candidateDTO);

        mockMvc.perform(patch("/candidates/{id}", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(candidateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"));

        verify(candidateSrv).update(eq(candidateId), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser
    void testPatchCandidateNotFound() throws Exception {
        when(candidateSrv.update(eq(candidateId), any(CandidateDTO.class))).thenReturn(null);

        mockMvc.perform(patch("/candidates/{id}", candidateId)
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
        when(candidateSrv.getAllCandidates()).thenReturn(candidateList);

        mockMvc.perform(get("/candidates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(candidateId.toString()))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[1].fullName").value("Jane Smith"));

        verify(candidateSrv).getAllCandidates();
    }

    @Test
    @WithMockUser
    void testGetAllCandidatesEmpty() throws Exception {
        when(candidateSrv.getAllCandidates()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/candidates"))
                .andExpect(status().isNotFound());

        verify(candidateSrv).getAllCandidates();
    }

    @Test
    @WithMockUser
    void testGetCandidateById() throws Exception {
        when(candidateSrv.getById(candidateId)).thenReturn(candidateDTO);

        mockMvc.perform(get("/candidates/{id}", candidateId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(candidateId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"));

        verify(candidateSrv).getById(candidateId);
    }

    @Test
    @WithMockUser
    void testGetCandidateByIdNotFound() throws Exception {
        when(candidateSrv.getById(candidateId)).thenReturn(null);

        mockMvc.perform(get("/candidates/{id}", candidateId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());

        verify(candidateSrv).getById(candidateId);
    }

    @Test
    @WithMockUser
    void testDeleteCandidate() throws Exception {
        when(candidateSrv.delete(candidateId)).thenReturn(true);

        mockMvc.perform(delete("/candidates/{id}", candidateId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Candidate deleted successfully"));

        verify(candidateSrv).delete(candidateId);
    }

    @Test
    @WithMockUser
    void testDeleteCandidateNotFound() throws Exception {
        when(candidateSrv.delete(candidateId)).thenReturn(false);

        mockMvc.perform(delete("/candidates/{id}", candidateId)
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
        Map<String, Long> skillsMap = new HashMap<>();
        skillsMap.put("Java", 5L);
        skillsMap.put("Python", 3L);
        skillsMap.put("Spring", 4L);

        when(candidateSrv.getCandidatesBySkill()).thenReturn(skillsMap);

        mockMvc.perform(get("/candidates/technologies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", containsInAnyOrder("Java", "Python", "Spring")));

        verify(candidateSrv).getCandidatesBySkill();
    }

    @Test
    @WithMockUser
    void testGetCandidatesByLanguage() throws Exception {
        // Create candidates with languages
        List<LanguageDTO> englishLanguages = Collections.singletonList(
                new LanguageDTO(UUID.randomUUID(), null, "English description", "English description", 
                        null, "English", "English", null, false)
        );

        CandidateDTO candidateWithEnglish = new CandidateDTO(
                UUID.randomUUID(),
                "John English",
                LocalDate.of(1990, 1, 1),
                5,
                GenderEnum.M,
                "Java",
                "Speaks English",
                null, null, null, null, null, null, englishLanguages
        );

        List<CandidateDTO> candidatesWithEnglish = Collections.singletonList(candidateWithEnglish);

        when(candidateSrv.getCandidates()).thenReturn(candidateList);
        // Use doReturn().when() syntax to avoid issues with matchers
        doReturn(candidatesWithEnglish).when(candidateSrv).getCandidates();

        mockMvc.perform(get("/candidates/languages/{lang}", "English")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName").value("John English"));

        verify(candidateSrv).getCandidates();
    }

    @Test
    @WithMockUser
    void testGetCandidatesBySkill() throws Exception {
        // Create candidates with skills
        List<SkillDTO> javaSkills = Collections.singletonList(
                new SkillDTO(UUID.randomUUID(), "Java", null)
        );

        CandidateDTO candidateWithJava = new CandidateDTO(
                UUID.randomUUID(),
                "Java Developer",
                LocalDate.of(1990, 1, 1),
                5,
                GenderEnum.M,
                "Java",
                "Java expert",
                null, null, javaSkills, null, null, null, null
        );

        List<CandidateDTO> candidatesWithJava = Collections.singletonList(candidateWithJava);

        // Use doReturn().when() syntax to avoid issues with matchers
        doReturn(candidatesWithJava).when(candidateSrv).getCandidates();

        mockMvc.perform(get("/candidates/skills/{skill}", "Java")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName").value("Java Developer"));

        verify(candidateSrv).getCandidates();
    }
}
