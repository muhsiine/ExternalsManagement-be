package ma.nttdata.externals.module.offer.mapper;

import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.springframework.stereotype.Component;

@Component
public class OfferMapperTest {

    public OfferDTO offerToOfferDTO(Offer offer) {
        if (offer == null) return null;
        return new OfferDTO(offer.getId(), offer.getTitle(), offer.getDescription());
    }

    public Offer offerDTOToOffer(OfferDTO dto) {
        if (dto == null) return null;
        Offer offer = new Offer();
        offer.setId(dto.id());
        offer.setTitle(dto.title());
        offer.setDescription(dto.description());
        return offer;
    }
}
