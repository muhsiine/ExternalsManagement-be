package ma.nttdata.externals.module.prompt.service;

import ma.nttdata.externals.module.prompt.dto.PromptDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromptService {
    PromptDTO createPrompt(PromptDTO promptDTO);
    Optional<PromptDTO> getPromptById(UUID id);
    List<PromptDTO> getAllPrompts();
    PromptDTO updatePrompt(UUID id, PromptDTO promptDTO);
    void deletePrompt(UUID id);
}