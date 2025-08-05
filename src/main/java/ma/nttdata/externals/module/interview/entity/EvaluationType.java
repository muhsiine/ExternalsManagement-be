package ma.nttdata.externals.module.interview.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "evaluation_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EvaluationType {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name="coefficient")
    private Double coefficient;

}
