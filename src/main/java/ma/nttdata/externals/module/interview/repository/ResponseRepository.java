package ma.nttdata.externals.module.interview.repository;

import ma.nttdata.externals.module.interview.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResponseRepository extends JpaRepository<Response, UUID> {
}