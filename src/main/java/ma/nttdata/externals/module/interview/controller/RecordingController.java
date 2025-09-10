package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.CreateRecordingRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordingDTO;
import ma.nttdata.externals.module.interview.dto.TranscriptFormattingRequestDTO;
import ma.nttdata.externals.module.interview.service.RecordingServ;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recordings")
@RequiredArgsConstructor
public class RecordingController {

    private final RecordingServ recordingServ;

    @Operation(summary = "create a record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "record created"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<RecordingDTO> createRecord(@Valid @RequestBody CreateRecordingRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordingServ.createRecordingAndReturnDTO(request));
    }

    @Operation(summary = "update a record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record updated"),
            @ApiResponse(responseCode = "404", description = "Record not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping
    public ResponseEntity<RecordingDTO> updateRecord(@Valid @RequestBody RecordingDTO request){
        try {
            RecordingDTO updatedRecord = recordingServ.updateRecording(request);
            return ResponseEntity.ok(updatedRecord);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "fetch a record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found"),
            @ApiResponse(responseCode = "404", description = "Record not found"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecordingDTO> findRecordById(@PathVariable UUID id){
        try {
            RecordingDTO record = recordingServ.findRecordingById(id);
            return ResponseEntity.ok(record);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "fetch all records")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found")
    })
    @GetMapping
    public ResponseEntity<List<RecordingDTO>> findAllRecords(){
        List<RecordingDTO> records = recordingServ.findAllRecordings();
        return ResponseEntity.ok(records);
    }

    @Operation(summary = "fetch record by interview Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @GetMapping("/by-interview/{interviewId}")
    public ResponseEntity<RecordingDTO> findRecordByInterviewId(@PathVariable UUID interviewId) {
        RecordingDTO record = recordingServ.findRecordingByInterviewId(interviewId);
        return record != null
                ? ResponseEntity.ok(record)
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "fetch all records by offer id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Records found")
    })
    @GetMapping("/by-offer/{offerId}")
    public ResponseEntity<List<RecordingDTO>> findAllRecordsByOfferId(@PathVariable UUID offerId) {
        return ResponseEntity.ok(recordingServ.findAllRecordingsByOfferId(offerId));
    }

    @Operation(summary = "delete a record")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Record deleted"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecordById(@PathVariable UUID id){
        try {
            recordingServ.deleteRecordingById(id);
            return ResponseEntity.noContent().build();
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "create recording and save transcript")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recording created and transcript saved"),
            @ApiResponse(responseCode = "404", description = "Interview not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error During the formatting of json")
    })
    @DeleteMapping("/saveTranscript/{interviewId}")
    public ResponseEntity<?> formatTranscriptAndCreateRecording(@PathVariable UUID interviewId, @RequestBody List<TranscriptFormattingRequestDTO> request){
        try {
            recordingServ.saveTranscriptAndCreateRecording(interviewId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body("transcript saved and recording created");
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch(RuntimeException exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during the formatting of the transcript: "+exception.getMessage());
        }
    }
}
