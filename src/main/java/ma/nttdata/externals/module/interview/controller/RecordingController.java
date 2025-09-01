package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.CreateRecordingRequestDTO;
import ma.nttdata.externals.module.interview.dto.MergeRecordingsRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordingDTO;
import ma.nttdata.externals.module.interview.dto.RecordingUploadRequestDTO;
import ma.nttdata.externals.module.interview.service.RecordingServ;
import ma.nttdata.externals.module.interview.service.RecordingUploadServ;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
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
    public ResponseEntity<RecordingDTO> createRecording(@Valid @RequestBody CreateRecordingRequestDTO request){
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
    public ResponseEntity<RecordingDTO> updateRecording(@Valid @RequestBody RecordingDTO request){
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
    public ResponseEntity<RecordingDTO> findRecordingById(@PathVariable UUID id){
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
    public ResponseEntity<List<RecordingDTO>> findAllRecordings(){
        List<RecordingDTO> records = recordingServ.findAllRecordings();
        return ResponseEntity.ok(records);
    }

    @Operation(summary = "fetch record by interview Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @GetMapping("/by-interview/{interviewId}")
    public ResponseEntity<RecordingDTO> findRecordingByInterviewId(@PathVariable UUID interviewId) {
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
    public ResponseEntity<List<RecordingDTO>> findAllRecordingsByOfferId(@PathVariable UUID offerId) {
        return ResponseEntity.ok(recordingServ.findAllRecordingsByOfferId(offerId));
    }

    @Operation(summary = "delete a record")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Record deleted"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecordingById(@PathVariable UUID id){
        try {
            recordingServ.deleteRecordingById(id);
            return ResponseEntity.noContent().build();
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "upload a chunk")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "chunk uploaded"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upload")
    public ResponseEntity<?> uploadRecording(@Valid @RequestBody RecordingUploadRequestDTO req){
        try {
            recordingServ.uploadChunk(req);
            return ResponseEntity.status(HttpStatus.CREATED).body("chunk uploaded");
        } catch (Exception e) {
            return ResponseEntity.status(500).body( e.getMessage());
        }
    }

    @Operation(summary = "mergeRecording and save to the database")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recording saved"),
            @ApiResponse(responseCode = "404", description = "Interview not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/merge")
    public ResponseEntity<?> mergeRecordingsAndCreateRecording(@Valid @RequestBody MergeRecordingsRequestDTO req){
        try {
            String fullRecordingUrl = recordingServ.mergeRecordingsAndCreateRecording(req);
            return ResponseEntity.status(HttpStatus.CREATED).body("Merged chunks into full recording and saved the recording to the database"+fullRecordingUrl);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body( e.getMessage());
        }
    }
}
