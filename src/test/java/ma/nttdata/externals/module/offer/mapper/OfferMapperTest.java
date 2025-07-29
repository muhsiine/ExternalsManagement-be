package ma.nttdata.externals.module.offer.mapper;

import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.interview.mapper.InterviewMapper;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OfferMapperTest {

    @Autowired
    private InterviewMapper interviewMapper;

    public OfferDTO offerToOfferDTO(Offer offer) {
        if (offer == null) return null;
        List<InterviewDTO> interviewDTOs = offer.getInterviews() != null
                ? interviewMapper.toDtoList(offer.getInterviews())
                : Collections.emptyList();
        return new OfferDTO(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                interviewDTOs
        );
    }

    public Offer offerDTOToOffer(OfferDTO dto) {
        if (dto == null) return null;
        Offer offer = new Offer();
        offer.setId(dto.id());
        offer.setTitle(dto.title());
        offer.setDescription(dto.description());
        offer.setInterviews(dto.interviews() != null
                ? interviewMapper.toEntityList(dto.interviews())
                : Collections.emptyList());
        return offer;
    }
}