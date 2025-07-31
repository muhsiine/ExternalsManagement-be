package ma.nttdata.externals.module.offer.service.impl;
import jakarta.transaction.Transactional;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.mapper.OfferMapper;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.offer.service.OfferServ;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OfferServImpl implements OfferServ {

    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    public OfferServImpl(OfferRepository offerRepository, OfferMapper offerMapper) {
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
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


}
