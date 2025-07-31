package ma.nttdata.externals.module.offer.service;

import ma.nttdata.externals.module.offer.dto.OfferDTO;

import java.util.List;
import java.util.UUID;

public interface OfferServ {

    OfferDTO createOffer(OfferDTO offerDTO);

    OfferDTO getOfferById(UUID id);

    List<OfferDTO> getAllOffers();

    OfferDTO updateOffer(UUID id, OfferDTO offerDTO);

    List<String> getDistinctTitles();

    void deleteOffer(UUID id);
}