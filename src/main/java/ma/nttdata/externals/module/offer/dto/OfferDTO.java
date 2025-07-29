package ma.nttdata.externals.module.offer.dto;

import ma.nttdata.externals.module.interview.dto.InterviewDTO;

import java.util.List;
import java.util.UUID;

public record OfferDTO(
        UUID id,
        String title,
        String description ,
        List<InterviewDTO> interviews

) {}