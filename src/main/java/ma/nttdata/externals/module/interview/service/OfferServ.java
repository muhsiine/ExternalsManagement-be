package ma.nttdata.externals.module.interview.service;

import ma.nttdata.externals.module.interview.dto.OfferDTO;

import java.util.List;
import java.util.UUID;

public interface OfferServ {

    OfferDTO createOffer(OfferDTO offerDTO);

    OfferDTO getOfferById(UUID id);

    List<OfferDTO> getAllOffers();

    OfferDTO updateOffer(UUID id, OfferDTO offerDTO);

    void deleteOffer(UUID id);
}