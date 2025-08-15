package ma.nttdata.externals.module.interview.dto;

import java.util.List;
import java.util.UUID;

public record GenerateInterviewQuestionsRequest(
        List<UUID> evaluationTypesIds
) {
}
