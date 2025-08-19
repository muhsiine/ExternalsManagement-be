package ma.nttdata.externals.module.offer.service;



import ma.nttdata.externals.module.offer.dto.OfferCandidatesDTO;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;

import java.util.List;

public interface CandidateMatchingServ {

    /**
     * Find recommended candidates for a given offer description.
     *
     * @param offer the formatted description of the offer (contains tech, skills, languages, experience, etc.)
     * @return a list of candidates that match the offer, mapped to OfferCandidatesDTO for frontend display
     */
    List<OfferCandidatesDTO> findRecommendedCandidates(OfferFormattedDescriptionDTO offer);
}
