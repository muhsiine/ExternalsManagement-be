package ma.nttdata.externals.module.offer.service.impl;

import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.mapper.OfferMapper;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.prompt.service.PromptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferSrvImplTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferMapper offerMapper;
    @Mock
    private RestClient aiRestClient;
    @Mock
    private PromptService promptService;


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
                "Looking for a React expert" ,
                null,
                Collections.emptyList()
        );
    }

    @Test
    void testCreateOffer() {
        offerServImpl = new OfferServImpl(
                offerRepository,
                offerMapper,
                true,
                aiRestClient,
                promptService
        );
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
        offerServImpl = new OfferServImpl(
                offerRepository,
                offerMapper,
                true,
                aiRestClient,
                promptService
        );

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerMapper.toDto(offer)).thenReturn(offerDTO);

        OfferDTO result = offerServImpl.getOfferById(offerId);

        assertNotNull(result);
        assertEquals(offerDTO, result);
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    void testGetAllOffers() {
        offerServImpl = new OfferServImpl(
                offerRepository,
                offerMapper,
                true,
                aiRestClient,
                promptService
        );

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
        offerServImpl = new OfferServImpl(
                offerRepository,
                offerMapper,
                true,
                aiRestClient,
                promptService
        );

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
        offerServImpl = new OfferServImpl(
                offerRepository,
                offerMapper,
                true,
                aiRestClient,
                promptService
        );

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        offerServImpl.deleteOffer(offerId);

        verify(offerRepository, times(1)).delete(offer);
    }
}
