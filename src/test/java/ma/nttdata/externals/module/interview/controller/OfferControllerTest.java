package ma.nttdata.externals.module.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.interview.dto.OfferDTO;
import ma.nttdata.externals.module.interview.service.OfferServ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
class OfferControllerTest {

    private static final String API_URL = "/api/v1/offers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OfferServ offerService;

    private OfferDTO offerDTO;
    private UUID offerId;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();
        offerDTO = new OfferDTO(
                offerId,
                "Java Developer",
                "Looking for a senior Java developer."
        );
    }

    @Test
    @WithMockUser
    void testCreateOffer() throws Exception {
        when(offerService.createOffer(any(OfferDTO.class))).thenReturn(offerDTO);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(offerId.toString()))
                .andExpect(jsonPath("$.title").value("Java Developer"))
                .andExpect(jsonPath("$.description").value("Looking for a senior Java developer."));

        verify(offerService).createOffer(any(OfferDTO.class));
    }

    @Test
    @WithMockUser
    void testGetOfferById() throws Exception {
        when(offerService.getOfferById(offerId)).thenReturn(offerDTO);

        mockMvc.perform(get(API_URL + "/" + offerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(offerId.toString()))
                .andExpect(jsonPath("$.title").value("Java Developer"))
                .andExpect(jsonPath("$.description").value("Looking for a senior Java developer."));

        verify(offerService).getOfferById(offerId);
    }

    @Test
    @WithMockUser
    void testUpdateOffer() throws Exception {
        when(offerService.updateOffer(eq(offerId), any(OfferDTO.class))).thenReturn(offerDTO);

        mockMvc.perform(put(API_URL + "/" + offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(offerId.toString()))
                .andExpect(jsonPath("$.title").value("Java Developer"))
                .andExpect(jsonPath("$.description").value("Looking for a senior Java developer."));

        verify(offerService).updateOffer(eq(offerId), any(OfferDTO.class));
    }

    @Test
    @WithMockUser
    void testDeleteOffer() throws Exception {
        doNothing().when(offerService).deleteOffer(offerId);

        mockMvc.perform(delete(API_URL + "/" + offerId))
                .andExpect(status().isOk())
                .andExpect(content().string("Offer deleted successfully"));

        verify(offerService).deleteOffer(offerId);
    }

    @Test
    @WithMockUser
    void testGetAllOffers() throws Exception {
        List<OfferDTO> offers = Arrays.asList(
                offerDTO,
                new OfferDTO(UUID.randomUUID(), "Python Developer", "Need a Python expert.")
        );

        when(offerService.getAllOffers()).thenReturn(offers);

        mockMvc.perform(get(API_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Developer"))
                .andExpect(jsonPath("$[0].description").value("Looking for a senior Java developer."))
                .andExpect(jsonPath("$[1].title").value("Python Developer"))
                .andExpect(jsonPath("$[1].description").value("Need a Python expert."));

        verify(offerService).getAllOffers();
    }
}