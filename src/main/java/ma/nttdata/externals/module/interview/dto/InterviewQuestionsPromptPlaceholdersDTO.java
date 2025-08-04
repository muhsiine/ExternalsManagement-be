package ma.nttdata.externals.module.interview.dto;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.offer.dto.OfferDTO;

public record InterviewQuestionsPromptPlaceholdersDTO(
        CandidateDTO candidate,
        OfferDTO offer,
        int numberOfQuestions,
        int estimatedDuration
) {
}
