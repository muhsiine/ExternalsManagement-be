package ma.nttdata.externals.module.prompt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.service.PromptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromptController.class)
class PromptControllerTest {

    public static final String API_URL = "/api/v1/prompts";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PromptService promptService;

    private PromptDTO promptDTO;
    private UUID promptId;

    @BeforeEach
    void setUp() {
        promptId = UUID.randomUUID();
        promptDTO = new PromptDTO(
                promptId,
                "CODE123",
                "This is a sample prompt",
                "sample_schema"
        );
    }

    @Test
    @WithMockUser
    void testCreatePrompt() throws Exception {
        when(promptService.createPrompt(any(PromptDTO.class))).thenReturn(promptDTO);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(promptDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(promptId.toString()))
                .andExpect(jsonPath("$.promptCode").value("CODE123"))
                .andExpect(jsonPath("$.promptDesc").value("This is a sample prompt"))
                .andExpect(jsonPath("$.schema").value("sample_schema"));

        verify(promptService).createPrompt(any(PromptDTO.class));
    }

    @Test
    @WithMockUser
    void testGetPromptById() throws Exception {
        when(promptService.getPromptById(promptId)).thenReturn(Optional.of(promptDTO));

        mockMvc.perform(get(API_URL + "/{id}", promptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(promptId.toString()));

        verify(promptService).getPromptById(promptId);
    }

    @Test
    @WithMockUser
    void testGetPromptById_NotFound() throws Exception {
        when(promptService.getPromptById(promptId)).thenReturn(Optional.empty());

        mockMvc.perform(get(API_URL + "/{id}", promptId))
                .andExpect(status().isNotFound());

        verify(promptService).getPromptById(promptId);
    }

    @Test
    @WithMockUser
    void testGetAllPrompts() throws Exception {
        List<PromptDTO> promptList = List.of(promptDTO);
        when(promptService.getAllPrompts()).thenReturn(promptList);

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(promptId.toString()));

        verify(promptService).getAllPrompts();
    }

    @Test
    @WithMockUser
    void testGetAllPromptsPaginated() throws Exception {
        Page<PromptDTO> promptPage = new PageImpl<>(List.of(promptDTO));
        when(promptService.getAllPromptsPaginated(any(Pageable.class))).thenReturn(promptPage);

        mockMvc.perform(get(API_URL + "/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(promptId.toString()))
                .andExpect(jsonPath("$.content[0].promptCode").value("CODE123"))
                .andExpect(jsonPath("$.content[0].promptDesc").value("This is a sample prompt"))
                .andExpect(jsonPath("$.content[0].schema").value("sample_schema"));

        verify(promptService).getAllPromptsPaginated(any(Pageable.class));
    }

    @Test
    @WithMockUser
    void testUpdatePrompt() throws Exception {
        when(promptService.getPromptById(promptId)).thenReturn(Optional.of(promptDTO));
        when(promptService.updatePrompt(eq(promptId), any(PromptDTO.class))).thenReturn(promptDTO);

        mockMvc.perform(put(API_URL + "/{id}", promptId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(promptDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(promptId.toString()))
                .andExpect(jsonPath("$.promptCode").value("CODE123"));

        verify(promptService).updatePrompt(eq(promptId), any(PromptDTO.class));
    }

    @Test
    @WithMockUser
    void testUpdatePrompt_NotFound() throws Exception {
        when(promptService.getPromptById(promptId)).thenReturn(Optional.empty());

        mockMvc.perform(put(API_URL + "/{id}", promptId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(promptDTO)))
                .andExpect(status().isNotFound());

        verify(promptService, never()).updatePrompt(any(), any());
    }

    @Test
    @WithMockUser
    void testDeletePrompt() throws Exception {
        when(promptService.getPromptById(promptId)).thenReturn(Optional.of(promptDTO));
        doNothing().when(promptService).deletePrompt(promptId);

        mockMvc.perform(delete(API_URL + "/{id}", promptId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(promptService).deletePrompt(promptId);
    }

    @Test
    @WithMockUser
    void testDeletePrompt_NotFound() throws Exception {
        when(promptService.getPromptById(promptId)).thenReturn(Optional.empty());

        mockMvc.perform(delete(API_URL + "/{id}", promptId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());

        verify(promptService, never()).deletePrompt(any());
    }
}
