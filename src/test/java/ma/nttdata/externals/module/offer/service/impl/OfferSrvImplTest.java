package ma.nttdata.externals.module.offer.service.impl;

import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.mapper.OfferMapper;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferServImplTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferMapper offerMapper;

    @InjectMocks
    private OfferServImpl offerServImpl;

    private Offer offer;
    private OfferDTO offerDTO;
    private UUID offerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        offerId = UUID.randomUUID();

        offer = new Offer();
        offer.setId(offerId);
        offer.setTitle("Frontend Developer");
        offer.setDescription("Looking for a React expert");

        offerDTO = new OfferDTO(
                offerId,
                "Frontend Developer",
                "Looking for a React expert"
        );
    }

    @Test
    void testCreateOffer() {
        when(offerMapper.toEntity(offerDTO)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(offer);
        when(offerMapper.toDto(offer)).thenReturn(offerDTO);

        OfferDTO result = offerServImpl.createOffer(offerDTO);

        assertNotNull(result);
        assertEquals(offerDTO, result);
        verify(offerRepository, times(1)).save(offer);
    }

    @Test
    void testGetOfferById() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerMapper.toDto(offer)).thenReturn(offerDTO);

        OfferDTO result = offerServImpl.getOfferById(offerId);

        assertNotNull(result);
        assertEquals(offerDTO, result);
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    void testGetAllOffers() {
        List<Offer> offers = Arrays.asList(offer);
        List<OfferDTO> offerDTOs = Arrays.asList(offerDTO);

        when(offerRepository.findAll()).thenReturn(offers);
        when(offerMapper.toDtoList(offers)).thenReturn(offerDTOs);

        List<OfferDTO> result = offerServImpl.getAllOffers();

        assertEquals(1, result.size());
        assertEquals(offerDTOs, result);
        verify(offerRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOffer() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerMapper.toEntity(offerDTO)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(offer);
        when(offerMapper.toDto(offer)).thenReturn(offerDTO);

        OfferDTO result = offerServImpl.updateOffer(offerId, offerDTO);

        assertNotNull(result);
        assertEquals(offerDTO, result);
        verify(offerRepository, times(1)).save(offer);
    }

    @Test
    void testDeleteOffer() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        offerServImpl.deleteOffer(offerId);

        verify(offerRepository, times(1)).delete(offer);
    }
}
