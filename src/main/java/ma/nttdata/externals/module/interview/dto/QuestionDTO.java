package ma.nttdata.externals.module.interview.dto;


import java.util.UUID;

public record QuestionDTO(
        UUID id,
        String description,
        Integer  durationInMinutes ,
        UUID interviewId ,
        UUID answerId

) {}