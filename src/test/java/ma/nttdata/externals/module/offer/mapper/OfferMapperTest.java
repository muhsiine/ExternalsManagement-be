package ma.nttdata.externals.module.offer.mapper;

import ma.nttdata.externals.commons.constants.OfferFormattedDescriptionPromptConstants;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OfferMapperTest {

    private final OfferMapper offerMapper = Mappers.getMapper(OfferMapper.class);

    @Test
    void testOfferToOfferDTO() {
        Offer offer = new Offer();
        offer.setId(java.util.UUID.randomUUID());
        offer.setTitle("Java Developer");
        offer.setDescription("Job description");
        offer.setInterviews(Collections.emptyList());

        OfferDTO dto = offerMapper.toDto(offer);

        assertNotNull(dto);
        assertEquals(offer.getId(), dto.id());
        assertEquals(offer.getTitle(), dto.title());
        assertEquals(offer.getDescription(), dto.description());
    }

    @Test
    void testOfferDTOToOffer() {
        OfferDTO dto = new OfferDTO(
                java.util.UUID.randomUUID(),
                "Java Developer",
                "Job description",
                "",
                Collections.emptyList()
        );

        Offer offer = offerMapper.toEntity(dto);

        assertNotNull(offer);
        assertEquals(dto.id(), offer.getId());
        assertEquals(dto.title(), offer.getTitle());
        assertEquals(dto.description(), offer.getDescription());
    }

    @Test
    void mapTOJson_should_return_valid_OfferFormattedDescriptionDTO(){

        OfferFormattedDescriptionDTO result = offerMapper.mapJsonToDTO(
                OfferFormattedDescriptionPromptConstants.JSON_MOCK);

        assertNotNull(result);
        assertThat(result.description()).isEqualTo("We are seeking a highly skilled Senior Java Developer to join our dynamic fintech team. The role involves designing and implementing scalable microservices, collaborating with cross-functional teams, and ensuring high-quality code standards. The candidate will contribute to architecture decisions, mentor junior developers, and help drive the adoption of best practices.");
    }
}
