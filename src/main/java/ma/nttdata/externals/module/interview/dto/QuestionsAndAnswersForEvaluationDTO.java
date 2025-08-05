package ma.nttdata.externals.module.interview.dto;

public record QuestionsAndAnswersForEvaluationDTO(
        String questionDescription,
        String answerDescription,
        Integer questionDuration,
        Integer answerDuration
) {
}
