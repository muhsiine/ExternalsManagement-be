package ma.nttdata.externals.module.candidate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.nttdata.externals.commons.exception.BadRequestException;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.candidate.service.CandidateSrv;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ma.nttdata.externals.module.candidate.dto.SkillDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/v1/candidates")
@Tag(name = "Candidate Management", description = "Operations related to candidate management")
@Slf4j
public class CandidateController {

    private final CandidateSrv candidateSrv;

    public CandidateController(CandidateSrv candidateSrv) {
        this.candidateSrv = candidateSrv;
    }

    //Create
    @Operation(summary = "Create a new candidate", description = "Creates a new candidate and returns its details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Candidate created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<CandidateDTO> createCandidate(@Valid @RequestBody CandidateDTO candidate) {
        CandidateDTO savedCandidate = candidateSrv.save(candidate);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
    }

    //Update
    @Operation(summary = "Update a candidate", description = "Updates an existing candidate by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CandidateDTO> updateCandidate(
            @Parameter(description = "ID of the candidate to update") @PathVariable UUID id,
            @Valid @RequestBody CandidateDTO candidateDTO) {

        CandidateDTO updatedCandidate = candidateSrv.update(id, candidateDTO);
        return ResponseEntity.ok(updatedCandidate);
    }

    //Get All
    @Operation(summary = "Get all candidates", description = "Retrieves a list of all candidates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved candidates",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CandidateDTO.class, type = "array"))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        return ResponseEntity.ok(candidateSrv.getAllCandidates());
    }

    // Get By ID
    @Operation(summary = "Get a candidate by ID", description = "Retrieves a candidate by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved candidate",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateDTO.class))),
            @ApiResponse(responseCode = "404", description = "Candidate not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> getCandidate(
            @Parameter(description = "ID of the candidate to retrieve") @PathVariable UUID id) {
        return ResponseEntity.ok(candidateSrv.getById(id));
    }

    //Delete
    @Operation(summary = "Delete a candidate", description = "Deletes a candidate by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted candidate"),
            @ApiResponse(responseCode = "404", description = "Candidate not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCandidate(
            @Parameter(description = "ID of the candidate to delete") @PathVariable UUID id) {
        candidateSrv.delete(id);
        return ResponseEntity.ok("Candidate deleted successfully");
    }

    // Technologies
    @Operation(summary = "Get all technologies", description = "Retrieves a list of all technologies used by candidates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved technologies",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/technologies")
    public ResponseEntity<List<String>> getAllTechnologies() {
        log.info("Retrieving all technologies");

        // Get skill counts map and extract the skill names
        Map<String, Long> skillsMap = candidateSrv.getCandidatesBySkill();
        List<String> skills = new ArrayList<>(skillsMap.keySet());

        log.info("Retrieved {} technologies", skills.size());
        return ResponseEntity.ok(skills);
    }

    // Candidates by Language
    @Operation(summary = "Get candidates by language", description = "Retrieves a list of candidates who speak the specified language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved candidates",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "No candidates found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/languages/{lang}")
    public ResponseEntity<List<CandidateDTO>> getCandidatesByLanguage(
            @Parameter(description = "Language to filter by") @PathVariable String lang) {
        log.info("Retrieving candidates by language: {}", lang);

        List<CandidateDTO> candidates = candidateSrv.getCandidatesByLanguage(lang);

        if (candidates.isEmpty()) {
            log.info("No candidates found with language: {}", lang);
            throw new ResourceNotFoundException("No candidates found with language: " + lang);
        }

        log.info("Retrieved {} candidates with language: {}", candidates.size(), lang);
        return ResponseEntity.ok(candidates);
    }

    //Candidates by Skill
    @Operation(summary = "Get candidates by skill", description = "Retrieves a list of candidates who have the specified skill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved candidates",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "No candidates found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/skills/{skill}")
    public ResponseEntity<List<CandidateDTO>> getCandidatesBySkill(
            @Parameter(description = "Skill to filter by") @PathVariable String skill) {
        log.info("Retrieving candidates by skill: {}", skill);

        if (skill == null || skill.trim().isEmpty()) {
            throw new BadRequestException("Skill parameter cannot be empty");
        }

        List<CandidateDTO> candidates = candidateSrv.getCandidatesBySkill(skill);

        if (candidates.isEmpty()) {
            log.info("No candidates found with skill: {}", skill);
            throw new ResourceNotFoundException("No candidates found with skill: " + skill);
        }

        log.info("Retrieved {} candidates with skill: {}", candidates.size(), skill);
        return ResponseEntity.ok(candidates);
    }

    // Statistics by Language
    @Operation(summary = "Get statistics of candidates by language", description = "Retrieves statistics of how many candidates speak each language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved language statistics",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/statistics/languages")
    public ResponseEntity<Map<String, Long>> getCandidateStatsByLanguages() {
        log.info("Retrieving candidate statistics by language");
        Map<String, Long> languageStats = candidateSrv.getCandidatesByLanguage();

        log.info("Retrieved statistics for {} languages", languageStats.size());
        return ResponseEntity.ok(languageStats);
    }
}