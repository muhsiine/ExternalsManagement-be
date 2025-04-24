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
@RequestMapping("/api/v1/candidates")
@Tag(name = "Candidate Management", description = "Operations related to candidate management")

public class CandidateController {

    private final CandidateSrv candidateSrv;

    public CandidateController(CandidateSrv candidateSrv) {
        this.candidateSrv = candidateSrv;
    }

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

    @GetMapping("/all")
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        List<CandidateDTO> candidates = candidateSrv.getAllCandidates();
        if (candidates == null || candidates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
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

    @GetMapping("/filter")
    public ResponseEntity<List<CandidateDTO>> filterCandidates(
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer yearsOfExperience) {
        
        List<CandidateDTO> filteredCandidates = candidateSrv.getCandidates();
        
        if (skills != null && !skills.isEmpty()) {
            String[] skillArray = skills.split(",");
            filteredCandidates = filteredCandidates.stream()
                .filter(candidate -> candidate.skills() != null && 
                    candidate.skills().stream()
                        .anyMatch(s -> {
                            for (String skill : skillArray) {
                                if (s.skillName().equalsIgnoreCase(skill.trim())) {
                                    return true;
                                }
                            }
                            return false;
                        }))
                .collect(Collectors.toList());
        }
        
        if (language != null && !language.isEmpty()) {
            filteredCandidates = filteredCandidates.stream()
                .filter(candidate -> candidate.naturalLanguages() != null && 
                    candidate.naturalLanguages().stream()
                        .anyMatch(l -> l.language().equalsIgnoreCase(language)))
                .collect(Collectors.toList());
        }
        
        if (yearsOfExperience != null) {
            filteredCandidates = filteredCandidates.stream()
                .filter(candidate -> candidate.yearsOfExperience() >= yearsOfExperience)
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(filteredCandidates);
    }
}