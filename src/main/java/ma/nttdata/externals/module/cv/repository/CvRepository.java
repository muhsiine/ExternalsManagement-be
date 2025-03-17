package ma.nttdata.externals.module.cv.repository;

import ma.nttdata.externals.module.cv.entity.CvFile;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CvRepository extends ListCrudRepository<CvFile, UUID> {
}
