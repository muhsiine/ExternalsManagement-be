package ma.nttdata.externals.module.interview.dto;

import ma.nttdata.externals.module.candidate.dto.CandidateWithoutInterviewDTO;
import ma.nttdata.externals.module.offer.dto.OfferWithoutInterviewDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewWithCandidateAndOfferDTO(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description ,
        String link  ,
        String feedback_general ,
        LocalDateTime scheduledAt ,
        String comment,
        int numberOfQuestions,
        int estimatedDuration,
        CandidateWithoutInterviewDTO candidate,
        OfferWithoutInterviewDTO offer
) {
}
