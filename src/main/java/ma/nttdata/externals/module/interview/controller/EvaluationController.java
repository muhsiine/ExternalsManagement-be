package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.interview.dto.EvaluationDTO;
import ma.nttdata.externals.module.interview.service.EvaluationServ;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
@Tag(name = "Evaluations", description = "Operations related to Evaluations")
public class EvaluationController {

    private final EvaluationServ evaluationServ;

    @Operation(summary = "Get all evaluations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    @GetMapping
    public ResponseEntity<List<EvaluationDTO>> getAllEvaluations() {
        return ResponseEntity.ok(evaluationServ.getAllEvaluations());
    }

    @Operation(summary = "Get an evaluation by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluation found"),
            @ApiResponse(responseCode = "404", description = "Evaluation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationDTO> getEvaluationById(@PathVariable UUID id) {
        return ResponseEntity.ok(evaluationServ.getEvaluationById(id));
    }

    @Operation(summary = "Create a new evaluation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<EvaluationDTO> createEvaluation(@RequestBody EvaluationDTO evaluationDTO) {
        return ResponseEntity.ok(evaluationServ.createEvaluation(evaluationDTO));
    }

    @Operation(summary = "Update an existing evaluation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluation updated successfully"),
            @ApiResponse(responseCode = "404", description = "Evaluation not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationDTO> updateEvaluation(@PathVariable UUID id, @RequestBody EvaluationDTO evaluationDTO) {
        return ResponseEntity.ok(evaluationServ.updateEvaluation(id, evaluationDTO));
    }


    @Operation(summary = "Delete an evaluation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evaluation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Evaluation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable UUID id) {
        evaluationServ.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }
}
