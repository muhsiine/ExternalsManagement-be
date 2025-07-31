package ma.nttdata.externals.module.interview.dto;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.offer.dto.OfferDTO;

import java.util.List;

public record GenerateQuestionsInfoDTO(
        CandidateDTO candidate,
        OfferDTO offer,
        List<EvaluationTypeDTO> evaluationType
) {
}
