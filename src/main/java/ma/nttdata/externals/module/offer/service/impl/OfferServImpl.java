package ma.nttdata.externals.module.offer.service.impl;
import jakarta.transaction.Transactional;
import ma.nttdata.externals.commons.constants.OfferFormattedDescriptionPromptConstants;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.mapper.OfferMapper;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.offer.service.OfferServ;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.service.PromptService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OfferServImpl implements OfferServ {

    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;
    private final boolean mockFlag;
    private final RestClient aiRestClient;
    private final PromptService promptService;

    public OfferServImpl(OfferRepository offerRepository, OfferMapper offerMapper,
                         @Value("${app.mock.flag}")boolean mockFlag,
                         @Qualifier("aiServiceClient") RestClient aiRestClient, PromptService promptService) {
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
        this.mockFlag = mockFlag;
        this.aiRestClient = aiRestClient;
        this.promptService = promptService;
    }


    // create srv
    @Override
    public OfferDTO createOffer(OfferDTO offerDTO) {
        Offer offer = offerMapper.toEntity(offerDTO);
        Offer savedOffer = offerRepository.save(offer);
        return offerMapper.toDto(savedOffer);
    }
    // by id srv
    @Override
    public OfferDTO getOfferById(UUID id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        System.out.println("Offer : "+offer);
        return offerMapper.toDto(offer);
    }

    // all srv
    @Override
    public List<OfferDTO> getAllOffers() {
        return offerMapper.toDtoList(offerRepository.findAll());
    }

    // update srv
    @Override
    public OfferDTO updateOffer(UUID id, OfferDTO offerDTO) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        Offer updatedOffer = offerMapper.toEntity(offerDTO);
        updatedOffer.setId(id);
        updatedOffer = offerRepository.save(updatedOffer);
        return offerMapper.toDto(updatedOffer);
    }

    // delete srv
    @Override
    public void deleteOffer(UUID id) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        offerRepository.delete(offer);

    }
    @Override
    public List<String> getDistinctTitles() {
        return offerRepository.findDistinctTitles();
    }

    @Override
    public String getOfferFormattedDescriptionFromAIByPrompt(PromptDTO prompt,String offerDescription){

        String promptDesc = prompt.promptDesc();

        promptDesc = promptDesc.replace(OfferFormattedDescriptionPromptConstants.JSON_MOCK_PLACEHOLDER,OfferFormattedDescriptionPromptConstants.JSON_MOCK)
                .replace(OfferFormattedDescriptionPromptConstants.JSON_SCHEMA_PLACEHOLDER,OfferFormattedDescriptionPromptConstants.JSON_SCHEMA)
                .replace(OfferFormattedDescriptionPromptConstants.OFFER_DESCRIPTION_PLACEHOLDER,offerDescription);

        return aiRestClient.post()
                .uri("/extractFormattedDescription")
                .body(promptDesc)
                .retrieve()
                .body(String.class);
    }

    @Override
    public OfferFormattedDescriptionDTO prepareFormattedDescriptionByPrompt(UUID offerID){
        PromptDTO prompt = promptService.findByPromptCode(OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE);
        OfferDTO offer ;

        try {
            offer = getOfferById(offerID);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found with id: " + offerID, e);
        }
        System.out.println("Interviews: " + offer.interviews());

        String offerDescription = offer.description();
        String formattedDescription = mockFlag ? OfferFormattedDescriptionPromptConstants.JSON_MOCK
                : getOfferFormattedDescriptionFromAIByPrompt(prompt,offerDescription);
        setFormattedDescription(offerID,formattedDescription);
        return offerMapper.mapJsonToDTO(formattedDescription);
    }

    @Override
    public OfferFormattedDescriptionDTO getFormattedDescription(UUID offerId){
        OfferDTO offer;
        try {
            offer = getOfferById(offerId);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found with id: " + offerId, e);
        }
        return offerMapper.mapJsonToDTO(offer.formattedDescription());
    }

    @Override
    public int setFormattedDescription(UUID offerID, String formattedDescription){
        int updatedRows = offerRepository.updateFormattedDescriptionById(offerID,formattedDescription);
        if (updatedRows == 0) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update formatted description for offer with id: " + offerID
            );
        }
        return updatedRows;
    }
}
