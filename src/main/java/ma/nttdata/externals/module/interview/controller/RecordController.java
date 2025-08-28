package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.commons.exception.ResourceNotFoundException;
import ma.nttdata.externals.module.interview.dto.CreateRecordRequestDTO;
import ma.nttdata.externals.module.interview.dto.RecordDTO;
import ma.nttdata.externals.module.interview.service.RecordServ;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordServ recordServ;

    @Operation(summary = "create a record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "record created"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<RecordDTO> createRecord(@Valid @RequestBody CreateRecordRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordServ.createRecordAndReturnDTO(request));
    }

    @Operation(summary = "update a record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record updated"),
            @ApiResponse(responseCode = "404", description = "Record not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping
    public ResponseEntity<RecordDTO> updateRecord(@Valid @RequestBody RecordDTO request){
        try {
            RecordDTO updatedRecord = recordServ.updateRecord(request);
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
    public ResponseEntity<RecordDTO> findRecordById(@PathVariable UUID id){
        try {
            RecordDTO record = recordServ.findRecordById(id);
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
    public ResponseEntity<List<RecordDTO>> findAllRecords(){
        List<RecordDTO> records = recordServ.findAllRecords();
        return ResponseEntity.ok(records);
    }

    @Operation(summary = "fetch record by interview Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @GetMapping("/by-interview/{interviewId}")
    public ResponseEntity<RecordDTO> findRecordByInterviewId(@PathVariable UUID interviewId) {
        RecordDTO record = recordServ.findRecordByInterviewId(interviewId);
        return record != null
                ? ResponseEntity.ok(record)
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "fetch all records by offer id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Records found")
    })
    @GetMapping("/by-offer/{offerId}")
    public ResponseEntity<List<RecordDTO>> findAllRecordsByOfferId(@PathVariable UUID offerId) {
        return ResponseEntity.ok(recordServ.findAllRecordsByOfferId(offerId));
    }

    @Operation(summary = "delete a record")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Record deleted"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecordById(@PathVariable UUID id){
        try {
            recordServ.deleteRecordById(id);
            return ResponseEntity.noContent().build();
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
