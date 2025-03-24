package ma.nttdata.externals.module.prompt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    private String promptDesc;

    public void setId(UUID id) {

    }
}