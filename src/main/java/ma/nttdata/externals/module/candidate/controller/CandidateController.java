package ma.nttdata.externals.module.candidate.controller;

import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class CandidateController {

    private final CandidateSrv candidateSrv;

    public CandidateController(CandidateSrv candidateSrv) {
        this.candidateSrv = candidateSrv;
    }

    @PostMapping
    public ResponseEntity<CandidateDTO> createCandidate(@RequestBody CandidateDTO candidateDTO) {
        CandidateDTO savedCandidate = candidateSrv.save(candidateDTO);
        return ResponseEntity.ok(savedCandidate);
    }

    @GetMapping
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        List<CandidateDTO> candidates = candidateSrv.getCandidates();
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/charts/candidates/total")
    public ResponseEntity<Long> getTotalCandidates() {
        Long total = candidateSrv.getTotalCandidates();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/charts/languages")
    public ResponseEntity<List<String>> getAllLanguages() {
        Map<String, Long> candidatesByLanguage = candidateSrv.getCandidatesByLanguage();
        List<String> languages = candidatesByLanguage.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        return ResponseEntity.ok(languages);
    }

    @GetMapping("/charts/technologies")
    public ResponseEntity<List<String>> getAllTechnologies() {
        Map<String, Long> candidatesBySkill = candidateSrv.getCandidatesBySkill();
        List<String> skills = candidatesBySkill.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/charts/candidates/language/{lang}")
    public ResponseEntity<List<CandidateDTO>> getCandidatesByLanguage(@PathVariable String lang) {
        List<CandidateDTO> candidates = candidateSrv.getCandidates().stream()
                .filter(candidate -> candidate.naturalLanguages() != null && candidate.naturalLanguages().stream()
                        .anyMatch(language -> language.language().equalsIgnoreCase(lang)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/charts/candidates/technology/{skill}")
    public ResponseEntity<List<CandidateDTO>> getCandidatesBySkill(@PathVariable String skill) {
        List<CandidateDTO> candidates = candidateSrv.getCandidates().stream()
                .filter(candidate -> candidate.skills() != null && candidate.skills().stream()
                        .anyMatch(s -> s.skillName().equalsIgnoreCase(skill)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(candidates);
    }
}