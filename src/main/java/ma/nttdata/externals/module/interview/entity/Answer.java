package  ma.nttdata.externals.module.interview.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Entity
@Table(name = "answers")
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;

    // duration in minutes
    @Column(name = "duration_in_minutes")
    private Integer durationInMinutes;




}