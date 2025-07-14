package ma.nttdata.externals.module.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.nttdata.externals.module.interview.dto.OfferDTO;
import ma.nttdata.externals.module.interview.service.OfferServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/offers")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Offer Management API", description = "This API exposes endpoints to manage offers")
public class OfferController {
    private final OfferServ offerServ;
    @Autowired
    public OfferController(OfferServ offerServ) {

        this.offerServ= offerServ;
    }

    @PostMapping
    @Operation(summary = "Create a new offer", description = "Creates a new job offer and returns the created offer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Offer created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OfferDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OfferDTO> createOffer(@Valid @RequestBody OfferDTO offerDTO) {
        OfferDTO createdOffer = offerServ.createOffer(offerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get an offer by ID", description = "Retrieves a job offer by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offer found"),
            @ApiResponse(responseCode = "404", description = "Offer not found")
    })
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable UUID id) {
        OfferDTO offer = offerServ.getOfferById(id);
        return ResponseEntity.ok(offer);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all offers", description = "Retrieves a list of all job offers")
    @ApiResponse(responseCode = "200", description = "List of offers retrieved successfully")
    public ResponseEntity<List<OfferDTO>> getAllOffers() {
        List<OfferDTO> offers = offerServ.getAllOffers();
        return ResponseEntity.ok(offers);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an offer", description = "Updates an existing job offer by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Offer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable UUID id, @Valid @RequestBody OfferDTO offerDTO) {
        OfferDTO updatedOffer = offerServ.updateOffer(id, offerDTO);
        return ResponseEntity.ok(updatedOffer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an offer", description = "Deletes a job offer by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Offer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Offer not found")
    })

    public ResponseEntity<Void> deleteOffer(@PathVariable UUID id) {
        offerServ.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}