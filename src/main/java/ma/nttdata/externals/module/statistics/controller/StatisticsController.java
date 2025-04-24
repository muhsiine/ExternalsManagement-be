package ma.nttdata.externals.module.statistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ma.nttdata.externals.module.statistics.dto.StatisticsDTO;
import ma.nttdata.externals.module.statistics.service.impl.StatisticsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/candidates/charts/candidates")
@Tag(name = "Statistics", description = "Endpoints pour les statistiques des candidats")
public class StatisticsController {

    private final StatisticsServiceImpl statisticsService;

    public StatisticsController(StatisticsServiceImpl statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "Obtenir les statistiques d'expérience", 
              description = "Récupère la distribution des années d'expérience des candidats")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = StatisticsDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/experience")
    public ResponseEntity<StatisticsDTO> getExperienceStats() {
        return ResponseEntity.ok(statisticsService.getExperienceStats());
    }
}