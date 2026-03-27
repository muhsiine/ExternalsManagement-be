package ma.nttdata.externals.module.offer.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class OfferRepositoryTest {

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldReturnDistinctTitles() {
        Offer o1 = new Offer();
        o1.setTitle("Java Dev");

        Offer o2 = new Offer();
        o2.setTitle("Java Dev"); // duplicate

        Offer o3 = new Offer();
        o3.setTitle("Frontend Dev");

        offerRepository.saveAll(List.of(o1, o2, o3));

        List<String> titles = offerRepository.findDistinctTitles();

        assertEquals(2, titles.size());
        assertTrue(titles.contains("Java Dev"));
        assertTrue(titles.contains("Frontend Dev"));
    }



    @Test
    @Transactional
    void shouldUpdateFormattedDescriptionById() {
        Offer offer = new Offer();
        offer.setTitle("Test");
        offer = offerRepository.save(offer);

        offerRepository.updateFormattedDescriptionById(
                offer.getId(),
                "Formatted Description"
        );

        entityManager.flush();
        entityManager.clear();

        Offer updated = offerRepository.findById(offer.getId()).orElseThrow();

        assertEquals("Formatted Description", updated.getFormattedDescription());
    }
}