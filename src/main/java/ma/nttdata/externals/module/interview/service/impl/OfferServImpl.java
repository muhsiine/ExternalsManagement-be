package ma.nttdata.externals.module.interview.service.impl;

import jakarta.transaction.Transactional;
import ma.nttdata.externals.module.interview.dto.OfferDTO;
import ma.nttdata.externals.module.interview.entity.Offer;
import ma.nttdata.externals.module.interview.mapper.OfferMapper;
import ma.nttdata.externals.module.interview.repository.OfferRepository;
import ma.nttdata.externals.module.interview.service.OfferServ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional

public class OfferServImpl implements OfferServ {
    private final OfferRepository offerRepository ;
    private final OfferMapper offerMapper ;

    public OfferServImpl(OfferRepository offerRepository, OfferMapper offerMapper) {
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
    }
    @Override
    public OfferDTO createOffer(OfferDTO offerDTO) {
        Offer offer = offerMapper.toEntity(offerDTO);
        Offer savedOffer = offerRepository.save(offer);
        return offerMapper.toDto(savedOffer);
    }

    @Override
    public OfferDTO getOfferById(UUID id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        return offerMapper.toDto(offer);
    }

    @Override
    public List<OfferDTO> getAllOffers() {
        return offerMapper.toDtoList(offerRepository.findAll());
    }

    @Override
    public OfferDTO updateOffer(UUID id, OfferDTO offerDTO) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        Offer updatedOffer = offerMapper.toEntity(offerDTO);
        updatedOffer.setId(id);
        updatedOffer = offerRepository.save(updatedOffer);
        return offerMapper.toDto(updatedOffer);
    }

    @Override
    public void deleteOffer(UUID id) {
        if (!offerRepository.existsById(id)) {
            throw new RuntimeException("Offer not found with id: " + id);
        }
        offerRepository.deleteById(id);
    }
}
