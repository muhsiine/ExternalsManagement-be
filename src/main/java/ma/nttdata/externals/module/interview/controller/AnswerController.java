package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.interview.dto.AnswerDTO;
import ma.nttdata.externals.module.interview.service.AnswerServ;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
@Tag(name = "Answers", description = "Operations related to Answers")
public class AnswerController {

    private final AnswerServ answerServ;

    @Operation(summary = "Get all answers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of answers")
    })
    @GetMapping
    public ResponseEntity<List<AnswerDTO>> getAllAnswers() {
        return ResponseEntity.ok(answerServ.getAllAnswers());
    }

    @Operation(summary = "Get an answer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer found"),
            @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnswerDTO> getAnswerById(@PathVariable UUID id) {
        return ResponseEntity.ok(answerServ.getAnswerById(id));
    }

    @Operation(summary = "Create a new answer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<AnswerDTO> createAnswer(@RequestBody AnswerDTO answerDTO) {
        return ResponseEntity.ok(answerServ.createAnswer(answerDTO));
    }

    @Operation(summary = "Update an existing answer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Answer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AnswerDTO> updateAnswer(@PathVariable UUID id, @RequestBody AnswerDTO answerDTO) {
        return ResponseEntity.ok(answerServ.updateAnswer(id, answerDTO));
    }

    @Operation(summary = "Delete an answer")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Answer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable UUID id) {
        answerServ.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }
}
