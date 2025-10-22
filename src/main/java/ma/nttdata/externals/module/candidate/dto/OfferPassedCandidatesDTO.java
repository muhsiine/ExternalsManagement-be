package ma.nttdata.externals.module.candidate.dto;


import ma.nttdata.externals.module.interview.dto.FullEvaluationDTO;

import java.util.List;
import java.util.UUID;

public record OfferPassedCandidatesDTO(
        UUID id,
        String fullName,
        AddressDTO address,
        List<FullEvaluationDTO> evaluations
){}

