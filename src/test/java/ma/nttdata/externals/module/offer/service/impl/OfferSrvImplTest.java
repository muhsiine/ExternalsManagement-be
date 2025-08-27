package ma.nttdata.externals.module.offer.service.impl;

import ma.nttdata.externals.commons.constants.OfferFormattedDescriptionPromptConstants;
import ma.nttdata.externals.module.candidate.constants.LanguageLevel;
import ma.nttdata.externals.module.candidate.dto.OfferFormattedDescriptionLanguageDTO;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import ma.nttdata.externals.module.offer.mapper.OfferMapper;
import ma.nttdata.externals.module.offer.repository.OfferRepository;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.service.PromptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

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
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private PromptService promptService;

    private OfferServImpl offerServImplWithMockFlagTrue;

    private OfferServImpl offerServImplWithMockFlagFalse;

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
        offerServImplWithMockFlagTrue = new OfferServImpl(
                offerRepository,
                offerMapper,
                true,
                aiRestClient,
                promptService
        );

        offerServImplWithMockFlagFalse = new OfferServImpl(
                offerRepository,
                offerMapper,
                false,
                aiRestClient,
                promptService
        );
    }

    @Test
    void testCreateOffer() {

        when(offerMapper.toEntity(offerDTO)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(offer);
        when(offerMapper.toDto(offer)).thenReturn(offerDTO);

        OfferDTO result = offerServImplWithMockFlagTrue.createOffer(offerDTO);

        assertNotNull(result);
        assertEquals(offerDTO, result);
        verify(offerRepository, times(1)).save(offer);
    }

    @Test
    void testGetOfferById() {

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerMapper.toDto(offer)).thenReturn(offerDTO);

        OfferDTO result = offerServImplWithMockFlagTrue.getOfferById(offerId);

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

        List<OfferDTO> result = offerServImplWithMockFlagTrue.getAllOffers();

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

        OfferDTO result = offerServImplWithMockFlagTrue.updateOffer(offerId, offerDTO);

        assertNotNull(result);
        assertEquals(offerDTO, result);
        verify(offerRepository, times(1)).save(offer);
    }

    @Test
    void testDeleteOffer() {

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        offerServImplWithMockFlagTrue.deleteOffer(offerId);

        verify(offerRepository, times(1)).delete(offer);
    }

    @Test
    void should_return_DistinctTitles(){

        List<String> distinctTitles = Arrays.asList("Frontend Developer", "Backend Developer");

        when(offerRepository.findDistinctTitles()).thenReturn(distinctTitles);

        List<String> result = offerServImplWithMockFlagTrue.getDistinctTitles();

        verify(offerRepository, times(1)).findDistinctTitles();
        assertEquals(distinctTitles, result);
    }

    @Test
    void should_getOfferFormattedDescriptionFromAIByPrompt(){
        PromptDTO prompt = new PromptDTO(UUID.randomUUID(), OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE,
                OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT,
                OfferFormattedDescriptionPromptConstants.JSON_SCHEMA);

        String offerDescription = "Looking for a React expert";

        String expectedFormattedDescription = """
        {
          "description": "Looking for a React expert",
          "mainTech": "React - Frontend",
          "skills": "React - JavaScript",
          "languages": [{"languageName": "English","level": "ADVANCED"}],
          "yearsOfExperience": 3,
          "mainResponsibilities": "Develop UI - Collaborate with team",
          "education": "Bachelor - Computer Science",
          "keywords": "React - Frontend - JavaScript"
        }
        """;

        when(aiRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/extractOfferFormattedDescription")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(expectedFormattedDescription);

        String result = offerServImplWithMockFlagFalse.getOfferFormattedDescriptionFromAIByPrompt(prompt, offerDescription);

        assertNotNull(result);
        assertEquals(expectedFormattedDescription, result);

        verify(aiRestClient).post();
        verify(requestBodyUriSpec).uri("/extractOfferFormattedDescription");
        verify(requestBodySpec).body(anyString());
        verify(requestBodySpec).retrieve();
        verify(responseSpec).body(String.class);
    }

    @Test
    void for_Mock_flag_true_prepareFormattedDescriptionByPrompt_should_return_offerFormattedDescription(){
        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE,
                OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT,
                OfferFormattedDescriptionPromptConstants.JSON_SCHEMA);

        when(promptService.findByPromptCode(OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE))
                .thenReturn(prompt);

        OfferFormattedDescriptionDTO mockOfferFormatted = new OfferFormattedDescriptionDTO(
                "We are seeking a highly skilled Senior Java Developer to join our dynamic fintech team. The role involves designing and implementing scalable microservices, collaborating with cross-functional teams, and ensuring high-quality code standards. The candidate will contribute to architecture decisions, mentor junior developers, and help drive the adoption of best practices.",
                "Full Stack Javascript",
                "Java - Spring Boot - Docker - Kubernetes - AWS - MySQL - Git - Agile - Team Leadership - Communication",
                List.of(
                        new OfferFormattedDescriptionLanguageDTO("English", LanguageLevel.ADVANCED),
                        new OfferFormattedDescriptionLanguageDTO("French", LanguageLevel.INTERMEDIATE)
                ),
                6,
                "Design and implement microservices architecture - Optimize application performance - Maintain CI/CD pipelines - Conduct code reviews and mentor junior developers - Collaborate with product managers and QA team - Participate in on-call rotation",
                "Bachelor in Computer Science - Master in Software Engineering",
                "Java - Spring Boot - Microservices - Docker - Kubernetes - Fintech - Agile - DevOps - AWS - Backend Development"
        );

        OfferServImpl spyOfferServ = spy(offerServImplWithMockFlagTrue);
        doReturn(offerDTO).when(spyOfferServ).getOfferById(offerId);
        when(offerMapper.mapJsonToDTO( OfferFormattedDescriptionPromptConstants.JSON_MOCK))
                .thenReturn(mockOfferFormatted);
        doReturn(1).when(spyOfferServ).setFormattedDescription(eq(offerId),eq(OfferFormattedDescriptionPromptConstants.JSON_MOCK));


        OfferFormattedDescriptionDTO   formattedDescription = spyOfferServ
                .prepareFormattedDescriptionByPrompt(offerId);

        assertNotNull(formattedDescription);
        assertEquals(mockOfferFormatted, formattedDescription);

        verify(spyOfferServ).getOfferById(offerId);
        verify(spyOfferServ).setFormattedDescription(eq(offerId), eq(OfferFormattedDescriptionPromptConstants.JSON_MOCK));
        verify(offerMapper).mapJsonToDTO(OfferFormattedDescriptionPromptConstants.JSON_MOCK);

    }

    @Test
    void when_mockFlag_false_prepareFormattedDescriptionByPrompt_should_return_realFormattedDescription() {
        UUID offerId = UUID.randomUUID();
        PromptDTO prompt = new PromptDTO(UUID.randomUUID(),
                OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE,
                OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT,
                OfferFormattedDescriptionPromptConstants.JSON_SCHEMA);

        OfferFormattedDescriptionDTO aiFormattedDTO = new OfferFormattedDescriptionDTO(
                "Formatted description from AI",
                "Full Stack Java",
                "Java - Spring Boot",
                List.of(new OfferFormattedDescriptionLanguageDTO("English", LanguageLevel.ADVANCED)),
                5,
                "Responsibilities",
                "Requirements",
                "Skills"
        );
        String aiJsonString = """
        {
          "description": "Formatted description from AI",
          "mainTech": "Full Stack Java",
          "skills": "Java - Spring Boot",
          "languages": [
            {"languageName": "English", "level": "ADVANCED"}
          ],
          "yearsOfExperience": 5,
          "mainResponsibilities": "Responsibilities",
          "education": "Requirements",
          "keywords": "Skills"
        }
        """;

        OfferServImpl spyOfferServ = spy(offerServImplWithMockFlagFalse);

        when(promptService.findByPromptCode(
                OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE))
                .thenReturn(prompt);
        doReturn(offerDTO).when(spyOfferServ).getOfferById(offerId);
        doReturn(aiJsonString).when(spyOfferServ)
                .getOfferFormattedDescriptionFromAIByPrompt(prompt, offerDTO.description());

        doReturn(1).when(spyOfferServ).setFormattedDescription(eq(offerId), eq(aiJsonString));

        when(offerMapper.mapJsonToDTO(aiJsonString)).thenReturn(aiFormattedDTO);

        OfferFormattedDescriptionDTO result = spyOfferServ.prepareFormattedDescriptionByPrompt(offerId);

        assertNotNull(result);
        assertEquals(aiFormattedDTO, result);
        verify(spyOfferServ).getOfferById(offerId);
        verify(spyOfferServ).getOfferFormattedDescriptionFromAIByPrompt(prompt, offerDTO.description());
        verify(spyOfferServ).setFormattedDescription(eq(offerId), eq(aiJsonString));
        verify(offerMapper).mapJsonToDTO(anyString());
    }


    @Test
    void when_getOfferById_throws_RuntimeException_then_prepareFormattedDescriptionByPrompt_throws_ResponseStatusException() {
        UUID offerId = UUID.randomUUID();

        when(promptService.findByPromptCode(OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE))
                .thenReturn(new PromptDTO(
                        UUID.randomUUID(),
                        OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT_CODE,
                        OfferFormattedDescriptionPromptConstants.OFFER_FORMATTED_DESCRIPTION_EXTRACTION_PROMPT,
                        OfferFormattedDescriptionPromptConstants.JSON_SCHEMA
                ));

        OfferServImpl spyOfferServ = spy(offerServImplWithMockFlagTrue);

        doThrow(new RuntimeException("Database error")).when(spyOfferServ).getOfferById(offerId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> spyOfferServ.prepareFormattedDescriptionByPrompt(offerId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Offer not found with id: " + offerId));
    }

    @Test
    void when_offer_exists_getFormattedDescription_should_returnDTO() {
        OfferServImpl spyOfferServ = spy(offerServImplWithMockFlagTrue);

        OfferFormattedDescriptionDTO expectedDTO = new OfferFormattedDescriptionDTO(
                "Formatted description from AI",
                "Full Stack Java",
                "Java - Spring Boot",
                List.of(new OfferFormattedDescriptionLanguageDTO("English", LanguageLevel.ADVANCED)),
                5,
                "Responsibilities",
                "Requirements",
                "Skills"
        );

        doReturn(offerDTO).when(spyOfferServ).getOfferById(offerId);
        when(offerMapper.mapJsonToDTO(offerDTO.formattedDescription()))
                .thenReturn(expectedDTO);

        OfferFormattedDescriptionDTO result = spyOfferServ.getFormattedDescription(offerId);

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(spyOfferServ).getOfferById(offerId);
        verify(offerMapper).mapJsonToDTO(offerDTO.formattedDescription());
    }

    @Test
    void when_offer_not_found_getFormattedDescription_should_throwResponseStatusException() {

        OfferServImpl spyOfferServ = spy(offerServImplWithMockFlagFalse);

        doThrow(new RuntimeException("not found"))
                .when(spyOfferServ).getOfferById(any(UUID.class));

        UUID offerId = UUID.randomUUID();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> spyOfferServ.getFormattedDescription(offerId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Offer not found with id"));

        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(spyOfferServ).getOfferById(captor.capture());
        assertEquals(offerId, captor.getValue());

    }

    @Test
    void when_update_successful_setFormattedDescription_should_returnUpdatedRows() {
        String formattedDescription = "{\"description\": \"AI formatted\"}";

        when(offerRepository.updateFormattedDescriptionById(offerId, formattedDescription))
                .thenReturn(1);

        int result = offerServImplWithMockFlagFalse.setFormattedDescription(offerId, formattedDescription);

        assertEquals(1, result);
        verify(offerRepository).updateFormattedDescriptionById(offerId, formattedDescription);
    }

    @Test
    void when_update_fails_setFormattedDescription_should_throwResponseStatusException() {
        String formattedDescription = "{\"description\": \"AI formatted\"}";

        when(offerRepository.updateFormattedDescriptionById(offerId, formattedDescription))
                .thenReturn(0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> offerServImplWithMockFlagFalse.setFormattedDescription(offerId, formattedDescription));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Failed to update formatted description"));
    }


}
