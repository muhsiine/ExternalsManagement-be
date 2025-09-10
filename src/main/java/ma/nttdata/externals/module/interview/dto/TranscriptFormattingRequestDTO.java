package ma.nttdata.externals.module.interview.dto;

import java.time.LocalTime;

public record TranscriptFormattingRequestDTO(
        String question,
        LocalTime questionTime,
        String answer,
        LocalTime answerTime
) {
}
