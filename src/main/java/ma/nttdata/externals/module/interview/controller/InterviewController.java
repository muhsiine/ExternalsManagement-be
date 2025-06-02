package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.InterviewRequestDTO;
import ma.nttdata.externals.module.interview.service.InterviewSrv;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/interview")
@RequiredArgsConstructor
@Tag(name = "Interview Generation", description = "Operations related to interview question generation")
public class InterviewController {

    private final InterviewSrv interviewSrv;

    @Operation(summary = "Generate interview questions", description = "Generates interview questions based on candidate profile and specified parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions generated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Candidate not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{candidateId}/generate")
    public ResponseEntity<String> generateInterviewQuestions(
            @Parameter(description = "ID of the candidate") @PathVariable UUID candidateId,
            @Parameter(description = "Interview generation request parameters")
            @Valid @RequestBody InterviewRequestDTO request) {
        log.info("Received request to generate interview questions for candidate ID: {}", candidateId);
        
        if (!candidateId.equals(request.candidateId())) {
            log.warn("Path variable candidateId {} does not match request candidateId {}", 
                    candidateId, request.candidateId());
            throw new IllegalArgumentException("Candidate ID in path does not match request body");
        }

        String questions = interviewSrv.generateInterviewQuestions(request);
        log.info("Successfully generated interview questions for candidate ID: {}", candidateId);
        return ResponseEntity.ok(questions);
    }
}
