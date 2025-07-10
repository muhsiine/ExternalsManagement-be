package  ma.nttdata.externals.module.interview.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "responses")
@Getter
@Setter
public class Response {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column
    private Float score;

}