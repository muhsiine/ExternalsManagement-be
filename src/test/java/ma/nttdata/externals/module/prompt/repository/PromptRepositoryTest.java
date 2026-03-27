package ma.nttdata.externals.module.prompt.repository;

import ma.nttdata.externals.module.prompt.entity.Prompt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromptRepositoryUnitTest {

    @Mock
    private PromptRepository promptRepository;

    @Test
    void shouldFindByPromptCode() {
        // Arrange
        String promptCode = "CV_EXTRACTION";

        Prompt prompt = new Prompt();
        prompt.setId(UUID.randomUUID());
        prompt.setPromptCode(promptCode);

        when(promptRepository.findByPromptCode(promptCode))
                .thenReturn(Optional.of(prompt));

        // Act
        Optional<Prompt> result = promptRepository.findByPromptCode(promptCode);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(promptCode, result.get().getPromptCode());

        verify(promptRepository).findByPromptCode(promptCode);
    }

    @Test
    void shouldReturnEmptyWhenPromptCodeNotFound() {
        String promptCode = "UNKNOWN_CODE";

        when(promptRepository.findByPromptCode(promptCode))
                .thenReturn(Optional.empty());

        Optional<Prompt> result = promptRepository.findByPromptCode(promptCode);

        assertFalse(result.isPresent());

        verify(promptRepository).findByPromptCode(promptCode);
    }
}