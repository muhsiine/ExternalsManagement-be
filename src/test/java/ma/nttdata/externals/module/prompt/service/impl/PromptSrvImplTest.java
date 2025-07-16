package ma.nttdata.externals.module.prompt.service.impl;

import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.entity.Prompt;
import ma.nttdata.externals.module.prompt.mapper.PromptMapper;
import ma.nttdata.externals.module.prompt.repository.PromptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptServiceImplTest {

    @Mock
    private PromptRepository promptRepository;

    @Mock
    private PromptMapper promptMapper;

    @InjectMocks
    private PromptServiceImpl promptService;

    private Prompt prompt;
    private PromptDTO promptDTO;
    private UUID promptId;

    @BeforeEach
    void setUp() {
        promptId = UUID.randomUUID();

        prompt = new Prompt();
        prompt.setId(promptId);
        prompt.setPromptCode("CODE123");
        prompt.setPromptDesc("Description of prompt");
        prompt.setSchema("{\"schema\": true}");

        promptDTO = new PromptDTO(promptId, "CODE123", "Description of prompt", "{\"schema\": true}");
    }

    @Test
    void testCreatePrompt() {
        when(promptMapper.toEntity(any(PromptDTO.class))).thenReturn(prompt);
        when(promptRepository.save(any(Prompt.class))).thenReturn(prompt);
        when(promptMapper.toDTO(any(Prompt.class))).thenReturn(promptDTO);

        PromptDTO result = promptService.createPrompt(promptDTO);

        assertNotNull(result);
        assertEquals(promptDTO.promptCode(), result.promptCode());
        verify(promptRepository).save(any(Prompt.class));
    }

    @Test
    void testGetPromptById_found() {
        when(promptRepository.findById(promptId)).thenReturn(Optional.of(prompt));
        when(promptMapper.toDTO(prompt)).thenReturn(promptDTO);

        Optional<PromptDTO> result = promptService.getPromptById(promptId);

        assertTrue(result.isPresent());
        assertEquals(promptDTO.promptCode(), result.get().promptCode());
    }

    @Test
    void testGetPromptById_notFound() {
        when(promptRepository.findById(promptId)).thenReturn(Optional.empty());

        Optional<PromptDTO> result = promptService.getPromptById(promptId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllPrompts() {
        List<Prompt> prompts = List.of(prompt);
        when(promptRepository.findAll()).thenReturn(prompts);
        when(promptMapper.toDTO(prompt)).thenReturn(promptDTO);

        List<PromptDTO> result = promptService.getAllPrompts();

        assertEquals(1, result.size());
        assertEquals(promptDTO.promptCode(), result.getFirst().promptCode());
    }

    @Test
    void testGetAllPromptsPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prompt> promptPage = new PageImpl<>(List.of(prompt));
        when(promptRepository.findAll(pageable)).thenReturn(promptPage);
        when(promptMapper.toDTO(prompt)).thenReturn(promptDTO);

        Page<PromptDTO> result = promptService.getAllPromptsPaginated(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(promptDTO.promptCode(), result.getContent().getFirst().promptCode());
    }

    @Test
    void testUpdatePrompt_found() {
        Prompt updatedPrompt = new Prompt();
        updatedPrompt.setId(promptId);
        updatedPrompt.setPromptCode("CODE123");
        updatedPrompt.setPromptDesc("Updated Desc");
        updatedPrompt.setSchema("{\"schema\": true}");

        PromptDTO updatedDTO = new PromptDTO(promptId, "CODE123", "Updated Desc", "{\"schema\": true}");

        when(promptRepository.findById(promptId)).thenReturn(Optional.of(prompt));
        when(promptMapper.updatePromptFromDto(promptDTO, prompt)).thenReturn(updatedPrompt);
        when(promptRepository.save(updatedPrompt)).thenReturn(updatedPrompt);
        when(promptMapper.toDTO(updatedPrompt)).thenReturn(updatedDTO);

        PromptDTO result = promptService.updatePrompt(promptId, promptDTO);

        assertNotNull(result);
        assertEquals("Updated Desc", result.promptDesc());
    }

    @Test
    void testUpdatePrompt_notFound() {
        when(promptRepository.findById(promptId)).thenReturn(Optional.empty());

        PromptDTO result = promptService.updatePrompt(promptId, promptDTO);

        assertNull(result);
    }

    @Test
    void testDeletePrompt() {
        doNothing().when(promptRepository).deleteById(promptId);

        assertDoesNotThrow(() -> promptService.deletePrompt(promptId));
        verify(promptRepository).deleteById(promptId);
    }
}
