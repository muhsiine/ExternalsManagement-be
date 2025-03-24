package ma.nttdata.externals.module.candidate.controller;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidates")
public class CandidateController {

    private final CandidateSrv candidateSrv;

    public CandidateController(CandidateSrv candidateSrv) {
        this.candidateSrv = candidateSrv;
    }

    @PostMapping
    public ResponseEntity<?> candidate(@RequestBody CandidateDTO candidate) {
        CandidateDTO savedCandidate = candidateSrv.save(candidate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCandidate);
    }

    @GetMapping
    public ResponseEntity<List<CandidateDTO>> getCandidates() {
        List<CandidateDTO> candidates = candidateSrv.getCandidates();
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> getCandidateById(@PathVariable UUID id) {
        CandidateDTO candidate = candidateSrv.getCandidateById(id);
        return ResponseEntity.ok(candidate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateDTO> updateCandidate(@PathVariable UUID id, @RequestBody CandidateDTO candidate) {
        CandidateDTO updatedCandidate = candidateSrv.updateCandidate(id, candidate);
        return ResponseEntity.ok(updatedCandidate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable UUID id) {
        candidateSrv.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CandidateDTO>> filterCandidates(
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer yearsOfExperience) {

        List<CandidateDTO> filteredCandidates = candidateSrv.filterCandidates(skills, language, yearsOfExperience);
        return ResponseEntity.ok(filteredCandidates);
    }
}