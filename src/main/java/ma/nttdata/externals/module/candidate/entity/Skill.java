package ma.nttdata.externals.module.candidate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ma.nttdata.externals.module.candidate.constants.ProficiencyLevel;

import java.util.UUID;

@Entity
@Table(name = "skills")
@Getter
@Setter
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "skill_name")
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level")
    private ProficiencyLevel proficiencyLevel;
}
