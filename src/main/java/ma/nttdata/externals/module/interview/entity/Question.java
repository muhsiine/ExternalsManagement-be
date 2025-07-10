package ma.nttdata.externals.module.interview.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @Column
    private String type;

    @Column
    private Integer points;

    @Column(name = "question_order")
    private Integer questionOrder;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> tags;
}