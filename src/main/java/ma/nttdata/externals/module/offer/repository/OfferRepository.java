package ma.nttdata.externals.module.offer.repository;

import ma.nttdata.externals.module.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {

    @Query("SELECT DISTINCT o.title FROM Offer o")
    List<String> findDistinctTitles();
}
