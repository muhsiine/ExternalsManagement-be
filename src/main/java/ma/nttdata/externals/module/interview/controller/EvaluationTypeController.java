package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.interview.dto.EvaluationTypeDTO;
import ma.nttdata.externals.module.interview.service.EvaluationTypeServ;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluation-types")
@RequiredArgsConstructor
@Tag(name = "Evaluation Types", description = "Operations related to Evaluation Types")
public class EvaluationTypeController {

    private final EvaluationTypeServ evaluationTypeServ;

    @Operation(summary = "Get all evaluation types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of evaluation types")
    })
    @GetMapping
    public ResponseEntity<List<EvaluationTypeDTO>> getAllEvaluationTypes() {
        return ResponseEntity.ok(evaluationTypeServ.getAllTypes());
    }

    @Operation(summary = "Get evaluation type by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "EvaluationType found"),
            @ApiResponse(responseCode = "404", description = "EvaluationType not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationTypeDTO> getEvaluationTypeById(@PathVariable UUID id) {
        return ResponseEntity.ok(evaluationTypeServ.getTypeById(id));
    }

    @Operation(summary = "Create a new evaluation type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "EvaluationType created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<EvaluationTypeDTO> createEvaluationType(@RequestBody EvaluationTypeDTO dto) {
        return ResponseEntity.ok(evaluationTypeServ.createType(dto));
    }

    @Operation(summary = "Update an existing evaluation type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "EvaluationType updated successfully"),
            @ApiResponse(responseCode = "404", description = "EvaluationType not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationTypeDTO> updateEvaluationType(@PathVariable UUID id, @RequestBody EvaluationTypeDTO dto) {
        return ResponseEntity.ok(evaluationTypeServ.updateType(id, dto));
    }

    @Operation(summary = "Delete an evaluation type")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "EvaluationType deleted successfully"),
            @ApiResponse(responseCode = "404", description = "EvaluationType not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluationType(@PathVariable UUID id) {
        evaluationTypeServ.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}
