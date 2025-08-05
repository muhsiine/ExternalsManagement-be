package ma.nttdata.externals.module.interview.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.offer.entity.Offer;

import java.time.LocalDateTime;
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

    @Column(name = "starttime")
    private LocalDateTime startTime;

    @Column(name = "endtime")
    private LocalDateTime endTime;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "link")
    private String link ;

    @Column(name = "feedback_general")
    private String feedback_general;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "comment")
    private String comment;

    @Column(name = "number_of_questions")
    int numberOfQuestions;

    @Column(name = "estimated_duration")
    int estimatedDuration;

    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "candidate_id" , nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;


}
