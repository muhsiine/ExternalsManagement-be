package ma.nttdata.externals.module.interview.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ma.nttdata.externals.module.candidate.entity.Candidate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "interviews")
@Getter
@Setter
public class Interview {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id" , nullable = false)
    private Candidate candidate;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column
    private String status;


}
