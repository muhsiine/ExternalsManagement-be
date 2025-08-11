package ma.nttdata.externals.module.interview.dto;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.offer.dto.OfferDTO;

import java.util.List;

public record PlaceholdersForInterviewEvaluationPromptDTO(
        CandidateDTO candidate,
        OfferDTO offer,
        List<String> evaluationsDescription,
        List<Evaluation> evaluations,
        List<EvaluationType> evaluationType
) {
}
