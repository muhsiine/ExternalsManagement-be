package ma.nttdata.externals.module.offer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.offer.dto.OfferCandidatesDTO;
import ma.nttdata.externals.module.offer.service.CandidateMatchingServ;
import ma.nttdata.externals.module.offer.dto.OfferFormattedDescriptionDTO;
import ma.nttdata.externals.module.offer.service.OfferServ;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Candidate Matching API", description = "This API exposes endpoints for matching offers with candidates")
public class CandidateMatchingController {

    private final CandidateMatchingServ candidateMatchingService;
    private final OfferServ offerServ;

    @GetMapping("/recommended-candidates/{offerId}")
    @Operation(
            summary = "Find recommended candidates for an offer",
            description = "Fetches the top recommended candidates that match the given offer's formatted description"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommended candidates retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Offer not found")
    })
    public ResponseEntity<List<OfferCandidatesDTO>> getRecommendedCandidates(@PathVariable UUID offerId) {

        OfferFormattedDescriptionDTO offerDesc = offerServ.getFormattedDescription(offerId);

        List<OfferCandidatesDTO> recommendedCandidates = candidateMatchingService.findRecommendedCandidates(offerDesc);

        return ResponseEntity.ok(recommendedCandidates);
    }
}
