package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.interview.dto.QuestionDTO;
import ma.nttdata.externals.module.interview.dto.TtsRequestDTO;
import ma.nttdata.externals.module.interview.service.QuestionServ;
import ma.nttdata.externals.module.interview.service.TextToSpeechServ;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Questions", description = "Operations related to Questions crud")
public class QuestionController {

    private final QuestionServ questionServ;
    private final TextToSpeechServ textToSpeechServ;

    @Operation(summary = "Get all questions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of questions")
    })
    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        return ResponseEntity.ok(questionServ.getAllQuestions());
    }

    @Operation(summary = "Get a question by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question found"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable UUID id) {
        return ResponseEntity.ok(questionServ.getQuestionById(id));
    }

    @Operation(summary = "Create a new question")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(questionServ.createQuestion(questionDTO));
    }

    @Operation(summary = "Update an existing question")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question updated successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable UUID id, @RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(questionServ.updateQuestion(id, questionDTO));
    }

    @Operation(summary = "Delete a question")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionServ.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generateAudio")
    public ResponseEntity<byte[]> generateQuestionAudio(@RequestBody TtsRequestDTO ttsRequestDTO){
        byte[] audio = textToSpeechServ.speak(ttsRequestDTO.text());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));
        headers.setContentLength(audio.length);

        return new ResponseEntity<>(audio,headers, HttpStatus.OK);
    }

}
