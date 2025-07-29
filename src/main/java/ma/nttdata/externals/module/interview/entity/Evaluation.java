package ma.nttdata.externals.module.interview.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "evaluations")
@Getter
@Setter
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
