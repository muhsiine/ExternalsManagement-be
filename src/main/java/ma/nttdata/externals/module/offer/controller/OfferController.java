package ma.nttdata.externals.module.offer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.nttdata.externals.module.offer.dto.OfferDTO;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;
import ma.nttdata.externals.module.offer.service.OfferServ;
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

    // create offer
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

    // get offer by id
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


    // all of
    @GetMapping("/all")
    @Operation(summary = "Get all offers", description = "Retrieves a list of all job offers")
    @ApiResponse(responseCode = "200", description = "List of offers retrieved successfully")
    public ResponseEntity<List<OfferDTO>> getAllOffers() {
        List<OfferDTO> offers = offerServ.getAllOffers();
        return ResponseEntity.ok(offers);
    }


    // update request
    @PutMapping("/{id}")
    @Operation(summary = "Update an offer", description = "Updates an existing job offer by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offer updated successfully !"),
            @ApiResponse(responseCode = "404", description = "Offer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable UUID id, @Valid @RequestBody OfferDTO offerDTO) {
        OfferDTO updatedOffer = offerServ.updateOffer(id, offerDTO);
        return ResponseEntity.ok(updatedOffer);
    }


    // delete request
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an offer", description = "Deletes a job offer by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Offer deleted successfully !"),
            @ApiResponse(responseCode = "404", description = "Offer not found")
    })
    public ResponseEntity<Void> deleteOffer(@PathVariable UUID id) {
        offerServ.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/titles")
    @Operation(summary = "Get all distinct offer titles", description = "Retrieves a list of unique offer titles from the system")
    @ApiResponse(responseCode = "200", description = "List of distinct titles retrieved successfully")
    public ResponseEntity<List<String>> getDistinctOfferTitles() {
        List<String> titles = offerServ.getDistinctTitles();
        return ResponseEntity.ok(titles);
    }

    @PostMapping("/{id}/prepare-formatted-description")
    @Operation(summary = "Prepare formatted description for an offer",
            description = "Generates a structured and formatted description for a specific job offer using AI and saves it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formatted description generated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OfferFormattedDescriptionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Offer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OfferFormattedDescriptionDTO> prepareFormattedDescription(@PathVariable UUID id) {
        OfferFormattedDescriptionDTO formattedDescriptionDTO = offerServ.prepareFormattedDescriptionByPrompt(id);
        return ResponseEntity.ok(formattedDescriptionDTO);
    }

    @GetMapping("/{id}/formatted-description")
    @Operation(summary = "Get formatted description for an offer",
            description = "Retrieves the previously generated formatted description of a specific job offer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formatted description retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OfferFormattedDescriptionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Offer not found")
    })
    public ResponseEntity<OfferFormattedDescriptionDTO> getFormattedDescription(@PathVariable UUID id) {
        OfferFormattedDescriptionDTO formattedDescriptionDTO = offerServ.getFormattedDescription(id);
        return ResponseEntity.ok(formattedDescriptionDTO);
    }


}