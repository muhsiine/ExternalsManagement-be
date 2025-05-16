package ma.nttdata.externals.module.candidate.repository;

import ma.nttdata.externals.module.candidate.entity.Language;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LanguageRepository extends ListCrudRepository<Language, UUID> {
    
    // Count candidates by language using JPQL
    // Returns a list of arrays containing [language, count]
    // Each candidate is counted only once per language
    
    @Query("SELECT l.language AS language, COUNT(DISTINCT l.candidate.id) AS count FROM Language l GROUP BY l.language")
    List<Object[]> countCandidatesByLanguage();
} 