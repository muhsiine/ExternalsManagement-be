package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;

import java.util.List;
import java.util.UUID;

public interface EvaluationTypeServ {
    List<EvaluationTypeDTO> getAllTypes();

    EvaluationTypeDTO getTypeById(UUID id);

    EvaluationTypeDTO createType(EvaluationTypeDTO dto);

    EvaluationTypeDTO updateType(UUID id, EvaluationTypeDTO dto);

    void deleteType(UUID id);

    List<EvaluationTypeDTO> findAllById(List<UUID> ids);
}
