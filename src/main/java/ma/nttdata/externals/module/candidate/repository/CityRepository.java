package ma.nttdata.externals.module.candidate.repository;

import ma.nttdata.externals.module.candidate.entity.City;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository extends ListCrudRepository<City, UUID> {
    Optional<City> findByName(String name);
}
