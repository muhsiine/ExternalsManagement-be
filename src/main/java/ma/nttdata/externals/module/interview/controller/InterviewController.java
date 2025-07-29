package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interviews")
@Tag(name = "Interview Management API", description = "This API exposes endpoints to manage interviews")
@RequiredArgsConstructor
public class InterviewController  {

    private final InterviewServ interviewServ;

    @Operation(
            summary = "Create a new interview",
            description = "Creates and returns a new interview based on the provided InterviewDTO"
    )
    @PostMapping
    public ResponseEntity<InterviewDTO> createInterview(@RequestBody InterviewDTO interviewDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewServ.createInterview(interviewDTO));
    }

    @Operation(
            summary = "Get all interviews",
            description = "Returns a list of all interviews in the system"
    )
    @GetMapping("/all")
    public ResponseEntity<List<InterviewDTO>> getAllInterviews() {
        return ResponseEntity.ok(interviewServ.getAllInterviews());
    }

    @Operation(
            summary = "Get interview by ID",
            description = "Fetches an interview by its unique ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<InterviewDTO> getInterviewById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(interviewServ.getInterviewById(id));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found", e);
        }
    }

    @Operation(
            summary = "Update interview",
            description = "Updates an existing interview by ID using provided data"
    )
    @PutMapping("/{id}")
    public ResponseEntity<InterviewDTO> updateInterview(
            @PathVariable UUID id,
            @RequestBody InterviewDTO interviewDTO) {
        try {
            return ResponseEntity.ok(interviewServ.updateInterview(id, interviewDTO));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found", e);
        }
    }

    @Operation(
            summary = "Delete interview",
            description = "Deletes the interview with the specified ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable UUID id) {
        try {
            interviewServ.deleteInterview(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found", e);
        }
    }

    @Operation(
            summary = "Get interviews by offer ID",
            description = "Returns a list of interviews associated with a specific offer ID"
    )
    @GetMapping("/offer/{offerId}")
    public ResponseEntity<List<InterviewDTO>> getInterviewsByOfferId(@PathVariable UUID offerId) {
        return ResponseEntity.ok(interviewServ.getInterviewsByOfferId(offerId));
    }

    @Operation(
            summary = "Get questions by interview ID",
            description = "Returns all questions linked to a specific interview"
    )
    @GetMapping("/{interviewId}/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByInterviewId(@PathVariable UUID interviewId) {
        return ResponseEntity.ok(interviewServ.getQuestionsByInterviewId(interviewId));
    }

    @Operation(
            summary = "Get answer of a question",
            description = "Returns the answer associated with a given question ID"
    )
    @GetMapping("/questions/{questionId}/answer")
    public ResponseEntity<AnswerDTO> getAnswerByQuestion(@PathVariable UUID questionId) {
        AnswerDTO answer = interviewServ.getAnswerByQuestionId(questionId);
        return ResponseEntity.ok(answer);
    }

    @Operation(
            summary = "Get candidate by interview ID",
            description = "Returns the candidate linked to a specific interview"
    )
    @GetMapping("/{interviewId}/candidate")
    public ResponseEntity<CandidateDTO> getCandidateByInterviewId(@PathVariable UUID interviewId) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(interviewServ.getCandidateByInterviewId(interviewId)) ;
    }

    @Operation(
            summary = "Get evaluations of an interview",
            description = "Fetches all evaluations that belong to a specific interview"
    )
    @GetMapping("/{interviewId}/evaluations")
    public ResponseEntity<List<EvaluationDTO>> getEvaluationsOfInterview(@PathVariable UUID interviewId) {
        return ResponseEntity.ok(interviewServ.getEvaluationsOfInterview(interviewId));
    }

    @Operation(
            summary = "Get evaluation type by evaluation ID",
            description = "Returns the evaluation type associated with a specific evaluation ID"
    )
    @GetMapping("/{id}/type")
    public ResponseEntity<EvaluationTypeDTO> getEvaluationType(@PathVariable UUID id) {
        return ResponseEntity.ok(interviewServ.getEvaluationTypeOfEvaluation(id));
    }
}
