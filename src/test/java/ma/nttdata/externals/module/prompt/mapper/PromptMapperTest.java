package ma.nttdata.externals.module.prompt.mapper;

import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.entity.Prompt;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PromptMapperTest {

    private final PromptMapper promptMapper = Mappers.getMapper(PromptMapper.class);

    @Test
    void testEntityToDTO() {
        // Given
        UUID id = UUID.randomUUID();
        Prompt prompt = new Prompt();
        prompt.setId(id);
        prompt.setPromptCode("PROMPT-001");
        prompt.setPromptDesc("This is a sample prompt.");
        prompt.setSchema("{\"type\": \"object\"}");

        // When
        PromptDTO dto = promptMapper.toDTO(prompt);

        // Then
        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("PROMPT-001", dto.promptCode());
        assertEquals("This is a sample prompt.", dto.promptDesc());
        assertEquals("{\"type\": \"object\"}", dto.schema());
    }

    @Test
    void testDTOToEntity() {
        // Given
        UUID id = UUID.randomUUID();
        PromptDTO dto = new PromptDTO(
                id,
                "PROMPT-002",
                "Another prompt for testing.",
                "{\"type\": \"array\"}"
        );

        // When
        Prompt prompt = promptMapper.toEntity(dto);

        // Then
        assertNotNull(prompt);
        assertEquals(id, prompt.getId());
        assertEquals("PROMPT-002", prompt.getPromptCode());
        assertEquals("Another prompt for testing.", prompt.getPromptDesc());
        assertEquals("{\"type\": \"array\"}", prompt.getSchema());
    }

    @Test
    void testUpdatePromptFromDTO() {
        // Given
        Prompt existingPrompt = new Prompt();
        existingPrompt.setId(UUID.randomUUID());
        existingPrompt.setPromptCode("OLD_CODE");
        existingPrompt.setPromptDesc("Old description");
        existingPrompt.setSchema("Old schema");

        PromptDTO updatedDto = new PromptDTO(
                null, // ID is not updated
                "NEW_CODE",
                "New description",
                "New schema"
        );

        // When
        Prompt updated = promptMapper.updatePromptFromDto(updatedDto, existingPrompt);

        // Then
        assertNotNull(updated);
        assertEquals(existingPrompt.getId(), updated.getId()); // ID should remain the same
        assertEquals("NEW_CODE", updated.getPromptCode());
        assertEquals("New description", updated.getPromptDesc());
        assertEquals("New schema", updated.getSchema());
    }
}
