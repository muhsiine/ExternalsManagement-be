package ma.nttdata.externals.module.interview.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ma.nttdata.externals.module.candidate.entity.Candidate;
import ma.nttdata.externals.module.offer.entity.Offer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "interviews")
@Getter
@Setter@ToString
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinColumn(name = "recording_id", nullable = true)
    private Recording recording;

    @ElementCollection
    @CollectionTable(name = "interview_transcriptions", joinColumns = @JoinColumn(name = "interview_id"))
    @Column(name = "transcription_line", columnDefinition = "TEXT")
    private List<String> transcription;
}
