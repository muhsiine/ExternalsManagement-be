package ma.nttdata.externals.module.interview.dto;

public record GenerateInterviewQuestionsAiRequest(
        String prompt,
        String jsonSchema
) {
}
