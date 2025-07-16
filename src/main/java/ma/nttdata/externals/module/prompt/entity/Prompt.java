package ma.nttdata.externals.module.prompt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "prompts")
@Getter
@Setter
public class Prompt {
    @Id
    @GeneratedValue
    private UUID id;

    private String promptCode;

    @Column(columnDefinition = "TEXT")
    private String promptDesc;

    @Column(columnDefinition = "TEXT")
    private String schema;

    public void setId(UUID id) {
        this.id = id;
    }
}