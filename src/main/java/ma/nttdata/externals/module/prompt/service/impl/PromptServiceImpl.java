package ma.nttdata.externals.module.prompt.service.impl;

import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.entity.Prompt;
import ma.nttdata.externals.module.prompt.mapper.PromptMapper;
import ma.nttdata.externals.module.prompt.repository.PromptRepository;
import ma.nttdata.externals.module.prompt.service.PromptService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PromptServiceImpl implements PromptService {

    private final PromptRepository promptRepository;
    private final PromptMapper promptMapper;

    public PromptServiceImpl(PromptRepository promptRepository, PromptMapper promptMapper) {
        this.promptRepository = promptRepository;
        this.promptMapper = promptMapper;
    }

    @Override
    public PromptDTO createPrompt(PromptDTO promptDTO) {
        Prompt prompt = promptMapper.toEntity(promptDTO);
        Prompt savedPrompt = promptRepository.save(prompt);
        return promptMapper.toDTO(savedPrompt);
    }

    @Override
    public Optional<PromptDTO> getPromptById(UUID id) {
        return promptRepository.findById(id).map(promptMapper::toDTO);
    }

    @Override
    public List<PromptDTO> getAllPrompts() {
        return promptRepository.findAll().stream()
                .map(promptMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PromptDTO updatePrompt(UUID id, PromptDTO promptDTO) {
        Prompt existingPrompt = promptRepository.findById(id).orElse(null);
        if (existingPrompt == null) {
            return null;
        }
        Prompt updatedPrompt = promptMapper.updatePromptFromDto(promptDTO, existingPrompt);
        updatedPrompt.setId(id);
        return promptMapper.toDTO(promptRepository.save(updatedPrompt));
    }

    @Override
    public void deletePrompt(UUID id) {
        promptRepository.deleteById(id);
    }
}