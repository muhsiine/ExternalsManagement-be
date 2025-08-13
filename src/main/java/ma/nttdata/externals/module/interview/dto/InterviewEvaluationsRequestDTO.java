package ma.nttdata.externals.module.interview.dto;

import java.util.List;

public record InterviewEvaluationsRequestDTO(
        String promptCode,
        List<QuestionsAndAnswersForEvaluationDTO> questionsAndAnswersForEvaluation
) {
}
