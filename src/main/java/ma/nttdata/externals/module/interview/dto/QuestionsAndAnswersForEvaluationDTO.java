package ma.nttdata.externals.module.interview.dto;

public record QuestionsAndAnswersForEvaluationDTO(
        String questionDescription,
        String answerDescription,
        Integer estimatedAnswerTime,
        Integer realAnswerTime
) {
}
