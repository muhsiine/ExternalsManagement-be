package ma.nttdata.externals.module.interview.service.impl;

import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.interview.mapper.EvaluationTypeMapper;
import ma.nttdata.externals.module.interview.repository.EvaluationTypeRepository;
import ma.nttdata.externals.module.interview.service.EvaluationTypeServ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationTypeServImpl implements EvaluationTypeServ {

    private final EvaluationTypeRepository evaluationTyperepository;
    private final EvaluationTypeMapper mapper;

    @Override
    public List<EvaluationTypeDTO> getAllTypes() {
        return evaluationTyperepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public EvaluationTypeDTO getTypeById(UUID id) {
        EvaluationType type = evaluationTyperepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationType not found with id: " + id));
        return mapper.toDto(type);
    }

    @Override
    public EvaluationTypeDTO createType(EvaluationTypeDTO dto) {
        EvaluationType type = mapper.toEntity(dto);
        EvaluationType saved = evaluationTyperepository.save(type);
        return mapper.toDto(saved);
    }

    @Override
    public EvaluationTypeDTO updateType(UUID id, EvaluationTypeDTO dto) {
        EvaluationType existing = evaluationTyperepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationType not found with id: " + id));

        existing.setDescription(dto.description());
        existing.setCoefficient(dto.coefficient());

        EvaluationType updated = evaluationTyperepository.save(existing);
        return mapper.toDto(updated);
    }

    @Override
    public void deleteType(UUID id) {
        EvaluationType type = evaluationTyperepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationType not found with id: " + id));
        evaluationTyperepository.delete(type);
    }

    @Override
    public List<EvaluationTypeDTO> findAllById(List<UUID> ids){
        List<EvaluationType> evaluationTypes = evaluationTyperepository.findAllById(ids);

        return evaluationTypes.stream()
                .map(mapper::toDto).collect(Collectors.toList());
    }
}
