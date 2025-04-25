package ma.nttdata.externals.module.candidate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/candidates")
@Tag(name = "Candidate Management and Statistics", description = "Operations related to candidate management and statistics")
public class CandidateController {

    private final CandidateSrv candidateSrv;

    public CandidateController(CandidateSrv candidateSrv) {
        this.candidateSrv = candidateSrv;
    }

    // === Candidate Management Endpoints ===

    @Operation(summary = "Create a new candidate", description = "Creates a new candidate and returns its details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Candidate created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> candidate(@RequestBody CandidateDTO candidate) {
        CandidateDTO savedCandidate = candidateSrv.save(candidate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCandidate);
    }

    @Operation(summary = "Update a candidate", description = "Updates an existing candidate by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCandidate(
            @Parameter(description = "ID of the candidate to update") @PathVariable UUID id,
            @RequestBody CandidateDTO candidateDTO) {
        CandidateDTO updatedCandidate = candidateSrv.update(id, candidateDTO);
        if (updatedCandidate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Candidate not found");
        }
        return ResponseEntity.ok(updatedCandidate);
    }

    @Operation(summary = "Partially update a candidate", description = "Updates specific fields of an existing candidate by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateDTO.class))),
            @ApiResponse(responseCode = "404", description = "Candidate not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchCandidate(
            @Parameter(description = "ID of the candidate to update") @PathVariable UUID id,
            @RequestBody CandidateDTO candidateDTO) {
        CandidateDTO updatedCandidate = candidateSrv.update(id, candidateDTO);
        if (updatedCandidate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Candidate not found");
        }
        return ResponseEntity.ok(updatedCandidate);
    }

    @Operation(summary = "Get all candidates", description = "Retrieves all candidates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved candidates",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        List<CandidateDTO> candidates = candidateSrv.getAllCandidates();
        if (candidates == null || candidates.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Return empty list with 200 OK
        }
        return ResponseEntity.ok(candidates);
    }

    @Operation(summary = "Get a candidate by ID", description = "Retrieves a candidate by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved candidate",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateDTO.class))),
            @ApiResponse(responseCode = "404", description = "Candidate not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCandidate(
            @Parameter(description = "ID of the candidate to retrieve") @PathVariable UUID id) {
        CandidateDTO candidate = candidateSrv.getById(id);
        if (candidate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Candidate not found");
        }
        return ResponseEntity.ok(candidate);
    }

    @Operation(summary = "Delete a candidate", description = "Deletes a candidate by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted candidate"),
            @ApiResponse(responseCode = "404", description = "Candidate not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(
            @Parameter(description = "ID of the candidate to delete") @PathVariable UUID id) {
        boolean deleted = candidateSrv.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Candidate not found");
        }
        return ResponseEntity.ok("Candidate deleted successfully");
    }

    // === Candidate Statistics Endpoints ===

    @Operation(summary = "Get total number of candidates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved total count")
    })
    @GetMapping("/charts/total")
    public ResponseEntity<Long> getTotalCandidates() {
        return ResponseEntity.ok(candidateSrv.getTotalCandidates());
    }

    @Operation(summary = "Get all languages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved languages")
    })
    @GetMapping("/charts/languages")
    public ResponseEntity<List<String>> getAllLanguages() {
        Map<String, Long> languagesMap = candidateSrv.getCandidatesByLanguage();
        List<String> languages = languagesMap.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        return ResponseEntity.ok(languages);
    }

    @Operation(summary = "Get all technologies/skills")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved technologies")
    })
    @GetMapping("/charts/technologies")
    public ResponseEntity<List<String>> getAllTechnologies() {
        Map<String, Long> skillsMap = candidateSrv.getCandidatesBySkill();
        List<String> skills = skillsMap.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    @Operation(summary = "Get candidates by language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved candidates by language")
    })
    @GetMapping("/charts/language/{lang}")
    public ResponseEntity<List<CandidateDTO>> getCandidatesByLanguage(@PathVariable String lang) {
        List<CandidateDTO> candidates = candidateSrv.getCandidates().stream()
                .filter(candidate -> candidate.naturalLanguages() != null && candidate.naturalLanguages().stream()
                        .anyMatch(language -> language.language().equalsIgnoreCase(lang)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(candidates);
    }

    @Operation(summary = "Get candidates by technology/skill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved candidates by technology")
    })
    @GetMapping("/charts/technology/{skill}")
    public ResponseEntity<List<CandidateDTO>> getCandidatesBySkill(@PathVariable String skill) {
        List<CandidateDTO> candidates = candidateSrv.getCandidates().stream()
                .filter(candidate -> candidate.skills() != null && candidate.skills().stream()
                        .anyMatch(s -> s.skillName().equalsIgnoreCase(skill)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(candidates);
    }
}