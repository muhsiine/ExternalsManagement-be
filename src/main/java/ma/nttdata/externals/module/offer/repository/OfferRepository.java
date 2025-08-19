package ma.nttdata.externals.module.offer.repository;

import ma.nttdata.externals.module.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {

    @Query("SELECT DISTINCT o.title FROM Offer o")
    List<String> findDistinctTitles();

    @Modifying
    @Query("UPDATE Offer o SET o.formattedDescription = :formattedDescription WHERE o.id = :offerId")
    int updateFormattedDescriptionById(@Param("offerId") UUID offerId, @Param("formattedDescription") String formattedDescription);
}
