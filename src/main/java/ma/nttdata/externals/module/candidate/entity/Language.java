package ma.nttdata.externals.module.candidate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ma.nttdata.externals.module.candidate.constants.LanguageLevel;

import java.util.UUID;

@Entity
@Table(name = "languages")
@Getter
@Setter
public class Language {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false, length = 100)
    private String englishDescription;

    @Column(length = 255)
    private String fullDescription;

    @Column(nullable = false, length = 100)
    private String language;

    @Column(nullable = false, length = 100)
    private String languageInEnglish;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private LanguageLevel level;

    @Column(nullable = false)
    private boolean isNative;

    // Add this method to match the expected getName() in CandidateSrvImpl
    public String getName() {
        return language; // Or use languageInEnglish, description, etc., depending on your preference
    }
}