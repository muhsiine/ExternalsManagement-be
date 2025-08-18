package ma.nttdata.externals.module.prompt.service;

import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.entity.Prompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromptService {
    PromptDTO createPrompt(PromptDTO promptDTO);
    Optional<PromptDTO> getPromptById(UUID id);
    List<PromptDTO> getAllPrompts();
    Page<PromptDTO> getAllPromptsPaginated(Pageable pageable);
    PromptDTO updatePrompt(UUID id, PromptDTO promptDTO);
    void deletePrompt(UUID id);
    PromptDTO findByPromptCode(String promptCode);
}