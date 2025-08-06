package ma.nttdata.externals.module.interview.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "evaluations")
@Getter
@Setter
@AllArgsConstructor@NoArgsConstructor@Builder
public class Evaluation {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name= "score")
    private Double score ;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @OneToOne(fetch =FetchType.EAGER)
    @JoinColumn(name="evaluation_type_id" , nullable = false)
    private EvaluationType evaluationType;



}
