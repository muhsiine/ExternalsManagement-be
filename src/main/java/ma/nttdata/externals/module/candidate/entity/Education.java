package ma.nttdata.externals.module.candidate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "educations")
@Getter
@Setter
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    private String institution;
    private String degree;
    @Column(name = "start_date")
    private String startDate;
    @Column(name = "end_date")
    private String endDate;

    private String diploma;
}
