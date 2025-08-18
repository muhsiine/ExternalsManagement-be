package ma.nttdata.externals.module.offer.service;

import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;

import java.util.List;
import java.util.UUID;

public interface OfferServ {

    OfferDTO createOffer(OfferDTO offerDTO);

    OfferDTO getOfferById(UUID id);

    List<OfferDTO> getAllOffers();

    OfferDTO updateOffer(UUID id, OfferDTO offerDTO);

    void deleteOffer(UUID id);

    List<String> getDistinctTitles();

    String getOfferFormattedDescriptionFromAIByPrompt(PromptDTO prompt,String offerDescription);

    OfferFormattedDescriptionDTO prepareFormattedDescriptionByPrompt(UUID offerID);

    OfferFormattedDescriptionDTO getFormattedDescription(UUID OfferId);

    int setFormattedDescription(UUID offerID, String formattedDescription);
}