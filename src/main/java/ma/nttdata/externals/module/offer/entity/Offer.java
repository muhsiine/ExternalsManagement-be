package ma.nttdata.externals.module.offer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ma.nttdata.externals.module.interview.entity.Interview;

import java.util.List;
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

    @Column(name = "formatted_description")
    String formattedDescription;

    // offer have many inter
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interview> interviews;


}
