package ma.nttdata.externals.module.offer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "offers")
@Getter
@Setter

public class Offer {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id ;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;


}
