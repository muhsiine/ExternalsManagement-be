package ma.nttdata.externals.module.candidate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String street;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 255)
    private String fullAddress;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToOne
    @JoinColumn(name = "candidate_id", nullable = false, unique = true)
    private Candidate candidate;


}