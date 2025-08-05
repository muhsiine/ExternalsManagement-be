package ma.nttdata.externals.module.interview.dto;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.EvaluationType;
import ma.nttdata.externals.module.offer.dto.OfferDTO;

import java.util.List;

public record InterviewEvaluationPlaceholders(
        CandidateDTO candidate,
        OfferDTO offer,
        int numberOfQuestion,
        int estimatedDuration,
        List<String> evaluationsDescription,
        List<Evaluation> evaluations,
        List<EvaluationType> evaluationType
) {
}
