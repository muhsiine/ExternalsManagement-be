package ma.nttdata.externals.module.offer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.service.OfferServ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
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
                "Looking for a senior Java developer.",
                Collections.emptyList()
        );
    }
    @Test
    @WithMockUser
    void testCreateOffer() throws Exception {
        // Fixed UUID for consistency with createInterview()
        UUID fixedOfferId = UUID.fromString("a12be5ab-1234-4cdf-b44c-a8210db3abcd");

        // Given: input DTO without ID (for creation)
        OfferDTO inputDto = new OfferDTO(
                null,
                "Java Developer",
                "Looking for a senior Java developer.",
                Collections.emptyList()
        );

        // Expected return from service after creation (with fixed ID)
        OfferDTO returnedDto = new OfferDTO(
                fixedOfferId,
                "Java Developer",
                "Looking for a senior Java developer.",
                Collections.emptyList()
        );

        // When
        when(offerService.createOffer(any(OfferDTO.class))).thenReturn(returnedDto);

        // Then
        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fixedOfferId.toString()))
                .andExpect(jsonPath("$.title").value("Java Developer"))
                .andExpect(jsonPath("$.description").value("Looking for a senior Java developer."));

        verify(offerService).createOffer(any(OfferDTO.class));
    }

    @Test
    @WithMockUser
    void testGetOfferById() throws Exception {
        // When
        when(offerService.getOfferById(offerId)).thenReturn(offerDTO);

        // Then
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
    void updateOffer() throws Exception {
        // dto given
        OfferDTO updatedDto = new OfferDTO(
                offerId,
                "Senior Java Developer",
                "Looking for a senior Java developer with 5+ years experience.",
                Collections.emptyList()
        );

        when(offerService.updateOffer(eq(offerId), any(OfferDTO.class))).thenReturn(updatedDto);

        // then
        mockMvc.perform(put("/api/v1/offers/{id}", offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(offerId.toString()))
                .andExpect(jsonPath("$.title").value("Senior Java Developer"))
                .andExpect(jsonPath("$.description").value("Looking for a senior Java developer with 5+ years experience."));

        verify(offerService).updateOffer(eq(offerId), any(OfferDTO.class));
    }



    @Test
    @WithMockUser
    void deleteOffer() throws Exception {

        doNothing().when(offerService).deleteOffer(offerId);

        // when
        mockMvc.perform(delete("/api/v1/offers/{id}", offerId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent()); // Expecting 204 No Content

        // Then
        verify(offerService).deleteOffer(offerId);
    }


    @Test
    @WithMockUser
    void testGetAllOffers() throws Exception {
        //w
        List<OfferDTO> offers = Arrays.asList(
                offerDTO,
                new OfferDTO(UUID.randomUUID(), "Python Developer", "Need a Python expert." ,     Collections.emptyList())

        );

        // When
        when(offerService.getAllOffers()).thenReturn(offers);

        // Then
        mockMvc.perform(get(API_URL + "/all") // Correct endpoint is /all
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Developer"))
                .andExpect(jsonPath("$[0].description").value("Looking for a senior Java developer."))
                .andExpect(jsonPath("$[1].title").value("Python Developer"))
                .andExpect(jsonPath("$[1].description").value("Need a Python expert."));

        verify(offerService).getAllOffers();
    }
}
