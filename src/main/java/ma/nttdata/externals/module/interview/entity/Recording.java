package ma.nttdata.externals.module.interview.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recording")
@Getter
@Setter
public class Recording {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "transcript")
    private String transcript;
}
