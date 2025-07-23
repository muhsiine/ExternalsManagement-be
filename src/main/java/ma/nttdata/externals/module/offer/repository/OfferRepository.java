package ma.nttdata.externals.module.offer.repository;
import ma.nttdata.externals.module.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer ,UUID> {
}