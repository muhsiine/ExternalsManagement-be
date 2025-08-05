package ma.nttdata.externals.module.interview.dto;

import ma.nttdata.externals.module.interview.entity.Evaluation;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record InterviewDTO(
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
        UUID offerId,
        UUID candidateId ,
        List<EvaluationDTO> evaluations ,
        List<QuestionDTO> questions
) {}