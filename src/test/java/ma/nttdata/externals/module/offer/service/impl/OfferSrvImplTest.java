package ma.nttdata.externals.module.offer.service.impl;

import jakarta.persistence.EntityNotFoundException;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.mapper.OfferMapperTest;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.offer.service.impl.OfferServImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferSrvImplTest {

    @Mock
    private OfferMapperTest offerMapper;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferServImpl offerServ;

    private Offer offer;
    private OfferDTO offerDTO;
    private UUID offerId;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();

        offer = new Offer();
        offer.setId(offerId);
        offer.setTitle("Java Developer");
        offer.setDescription("Looking for a senior Java developer.");


        offerDTO = new OfferDTO(
                offerId,
                "Java Developer",
                "Looking for a senior Java developer."
        );
    }

    @Test
    void testCreateOffer() {
        when(offerMapper.offerToOfferDTO(any(Offer.class))).thenReturn(offerDTO);
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);
        when(offerMapper.offerToOfferDTO(any(Offer.class))).thenReturn(offerDTO);

        OfferDTO result = offerServ.createOffer(offerDTO);

        assertNotNull(result);
        assertEquals(offerDTO.id(), result.id());
        assertEquals(offerDTO.title(), result.title());
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void testUpdateOffer() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);
        when(offerMapper.offerToOfferDTO(any(Offer.class))).thenReturn(offerDTO);

        OfferDTO result = offerServ.updateOffer(offerId, offerDTO);

        assertNotNull(result);
        assertEquals(offerDTO.id(), result.id());
        assertEquals(offerDTO.title(), result.title());
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void testUpdateOfferNotFound() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> offerServ.updateOffer(offerId, offerDTO));
        verify(offerRepository, never()).save(any(Offer.class));
    }

    @Test
    void testGetOfferById() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerMapper.offerToOfferDTO(any(Offer.class))).thenReturn(offerDTO);

        OfferDTO result = offerServ.getOfferById(offerId);

        assertNotNull(result);
        assertEquals(offerDTO.id(), result.id());
        assertEquals(offerDTO.title(), result.title());
    }

    @Test
    void testGetOfferByIdNotFound() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> offerServ.getOfferById(offerId));
    }

    @Test
    void testGetAllOffers() {
        List<Offer> offers = List.of(offer);
        when(offerRepository.findAll()).thenReturn(offers);
        when(offerMapper.offerToOfferDTO(any(Offer.class))).thenReturn(offerDTO);

        List<OfferDTO> result = offerServ.getAllOffers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(offerDTO.id(), result.getFirst().id());
    }

    @Test
    void testDeleteOffer() {
        when(offerRepository.existsById(offerId)).thenReturn(true);

        offerServ.deleteOffer(offerId);

        verify(offerRepository).deleteById(offerId);
    }

    @Test
    void testDeleteOfferNotFound() {
        when(offerRepository.existsById(offerId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> offerServ.deleteOffer(offerId));
        verify(offerRepository, never()).deleteById(any());
    }
}
