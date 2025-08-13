package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.services.EmailContentBuilder;
import ma.nttdata.externals.commons.services.EmailService;
import ma.nttdata.externals.module.candidate.dto.CandidateDTO;
import ma.nttdata.externals.module.interview.dto.*;
import ma.nttdata.externals.module.interview.entity.Evaluation;
import ma.nttdata.externals.module.interview.entity.Question;
import ma.nttdata.externals.module.interview.service.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/interviews")
@Tag(name = "Interview Management API", description = "This API exposes endpoints to manage interviews")
@RequiredArgsConstructor
public class InterviewController  {

    private final InterviewServ interviewServ;
    private final QuestionServ questionServ;
    private final InterviewTokenServ interviewTokenServ;
    private final EmailService emailService;
    private final EmailContentBuilder emailContentBuilder;
    private final EvaluationTypeServ evaluationTypeServ;
    private final EvaluationServ evaluationServ;

    private final InterviewEvaluationUtilServ interviewEvaluationUtilServ;

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
    @GetMapping()
    public ResponseEntity<List<InterviewListDTO>> getAllInterviews() {
        return ResponseEntity.ok(interviewServ.getAllInterviewList());
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
            summary = "Add a comment to an interview",
            description = "Updates the comment field of an interview by ID"
    )
    @PutMapping("/{id}/add-comment")
    public ResponseEntity<InterviewDTO> addComment(
            @PathVariable UUID id,
            @RequestBody CommentRequestDTO commentRequestDTO) {
        try {
            InterviewDTO updatedInterview = interviewServ.addCommentToInterview(id, commentRequestDTO.comment());
            return ResponseEntity.ok(updatedInterview);
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
            summary = "Get evaluation type by evaluation ID",
            description = "Returns the evaluation type associated with a specific evaluation ID"
    )
    @GetMapping("/{id}/type")
    public ResponseEntity<EvaluationTypeDTO> getEvaluationType(@PathVariable UUID id) {
        return ResponseEntity.ok(interviewServ.getEvaluationTypeOfEvaluation(id));
    }

    @Operation(
            summary = "Generate and save interview link",
            description = "Generates a secure interview token, saves the interview link, and returns the full URL for the given interview ID"
    )
    @PostMapping("/{interviewId}/generateAndSaveLink")
    public ResponseEntity<String> generateAndSaveInterviewLink(@PathVariable UUID interviewId){
        LocalDateTime scheduledAt = interviewServ.getInterviewById(interviewId).scheduledAt();
        String token = interviewTokenServ.generateToken(scheduledAt);
        String interviewLink =  interviewServ.saveInterviewLink(token,interviewId);
        return ResponseEntity.ok(interviewLink);
    }

    @Operation(
            summary = "Send interview invitation email",
            description = "Sends an email to the candidate containing the interview link and scheduled date."
    )
    @PostMapping("/{interviewId}/sendEmail")
    public ResponseEntity<String> sendEmail(@PathVariable UUID interviewId){

           SendEmailDTO payload = interviewServ.getEmailInfo(interviewId);

            String html = emailContentBuilder.buildInterviewEmail(
                    payload.candidateFullName(),
                    payload.offerTitle(),
                    payload.link(),
                    payload.scheduledDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
            );

            emailService.sendEmail(payload.email(), "Your Interview at NTT DATA", html);
            return ResponseEntity.ok("Email sent successfully!");
    }


    @Operation(
            summary = "Generate Interview questions by interview Id",
            description = "It sends a prompt to the AI and then get The question"
    )
    @PostMapping("/{interviewId}/generateQuestions")
    public ResponseEntity<List<QuestionDTO>> generateInterviewQuestions(@PathVariable UUID interviewId,
                                                                   @RequestBody GenerateInterviewQuestionsRequest generateInterviewQuestionsRequest) {
        placeholdersForInterviewQuestionsPromptDTO placeholders = interviewServ.getPlaceholdersForInterviewQuestionsPrompt(interviewId);
        List<EvaluationTypeDTO> evaluationTypes = evaluationTypeServ.findAllById(generateInterviewQuestionsRequest.evaluationTypesIds());

        List<QuestionDTO> generatedQuestions = questionServ.prepareQuestionsFromAIResponse( placeholders, evaluationTypes);
        List<QuestionDTO> generatedQuestionsWithInterviewId = generatedQuestions.stream()
                .map(q -> new QuestionDTO(
                        null,
                        q.description(),
                        q.durationInMinutes(),
                        interviewId,
                        null
                ))
                .toList();
        List<QuestionDTO> savedQuestions = questionServ.saveAllQuestions(generatedQuestionsWithInterviewId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuestions);
    }

    @Operation(
            summary = "Get interview questions by interview Id",
            description = "Return the questions of that interview"
    )
    @GetMapping("/{interviewId}/getQuestions")
    public ResponseEntity<List<Question>> getInterviewQuestions(UUID interviewId){
        return ResponseEntity.ok(questionServ.findAllQuestionsByInterviewId(interviewId));
    }

    @Operation(
            summary = "Get interview questions by interview Id",
            description = "Return the DTO of questions of that interview"
    )
    @GetMapping("/{interviewId}/getQuestionsDTO")
    public ResponseEntity<List<QuestionDTO>> getInterviewQuestionsDTOS(UUID interviewId){

        return ResponseEntity.ok(questionServ.findAllQuestionsDTOSByInterviewId(interviewId));
    }

    @Operation(
            summary = "Generates interview evaluations using AI ",
            description = """
        This endpoint receives a list of questions and candidate answers for a specific interview, 
        and calls an AI service to generate evaluations based on multiple criteria 
        (e.g., time management, technical accuracy, job alignment).

        The AI returns scores and feedback for each evaluation type defined in the interview context. 
        These evaluations are then persisted to the database.
        """
    )
    @PostMapping("/{interviewId}/evaluations")
    public ResponseEntity<?> prepareInterviewEvaluation(@PathVariable UUID interviewId,@RequestBody InterviewEvaluationsRequestDTO interviewEvaluationsRequest){
        List<Evaluation> savedEvaluations = interviewEvaluationUtilServ.prepareInterviewEvaluation(interviewId,interviewEvaluationsRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Evaluation is created and saved");
    }


    @Operation(
            summary = "Get all the evaluations of an interview",
            description = "Return the evaluations of an interview alongside with their evaluationTypes"
    )
    @GetMapping("/{interviewId}/evaluations")
    public ResponseEntity<List<EvaluationWithInterviewAndEvaluationTypeDTO>> getInterviewEvaluations(@PathVariable UUID interviewId){
        return ResponseEntity.ok(evaluationServ.getAllEvaluationsWithInterviewByInterviewId(interviewId));
    }
}
