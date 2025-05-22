package ma.nttdata.externals.module.prompt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ma.nttdata.externals.module.prompt.dto.PromptDTO;
import ma.nttdata.externals.module.prompt.service.PromptService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prompts")
@Tag(name= "Prompt Management", description = "Operations related to prompt management")
public class PromptController {

    private final PromptService promptService;

    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }


    @Operation(
            summary = "Create a new Prompt",
            description = "Creates a new Prompt and returns its details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prompt created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromptDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content=@Content(schema = @Schema(type="object"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content=@Content(schema = @Schema(type="object")))
    })

    @PostMapping
    public ResponseEntity<PromptDTO> createPrompt(@RequestBody PromptDTO promptDTO) {
        PromptDTO createdPrompt = promptService.createPrompt(promptDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdPrompt);
    }


    @Operation(summary = "Get a Prompt by ID", description = "Retrieves a Prompt by their ID")
    @ApiResponses(value={
            @ApiResponse(responseCode="200" ,description ="Prompt retrieves successfully",
                    content = @Content(mediaType="Application/json" ,schema=@Schema(implementation=PromptDTO.class))),
            @ApiResponse(responseCode = "400" ,description = "Invalid input",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode = "404" ,description = "Prompt not found",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode = "500" ,description = "Internal server error",content = @Content(schema=@Schema(type="object")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PromptDTO> getPromptById(@PathVariable UUID id) {
        return promptService.getPromptById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Get all Prompts", description = "Retrieves all Prompts")
    @ApiResponses(value={
            @ApiResponse(responseCode="200" ,description = "Prompts retrieved successfully",content=@Content(mediaType = "Application/json", array = @ArraySchema(schema = @Schema(implementation = PromptDTO.class)))),
            @ApiResponse(responseCode="400",description="Invalid input",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode="404",description="Not found",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode="500",description="Internal server error",content = @Content(schema=@Schema(type="object")))
    })
    @GetMapping
    public ResponseEntity<List<PromptDTO>> getAllPrompts() {
        List<PromptDTO> prompts = promptService.getAllPrompts();
        return ResponseEntity.ok(prompts);
    }

    @Operation(summary = "Get all Prompts paginated", description = "Retrieves all Prompts paginated")
    @ApiResponses(value={
            @ApiResponse(responseCode="200" ,description = "Prompts retrieved successfully",content=@Content(mediaType = "Application/json", schema=@Schema(implementation= Page.class))),
            @ApiResponse(responseCode="404",description="Not found",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode="500",description="Internal server error",content = @Content(schema=@Schema(type="object")))
    })
    @GetMapping("/paginated")
    public ResponseEntity<Page<PromptDTO>> getAllPromptsPaginated(@PageableDefault(size = 10) Pageable pageable) {
        Page<PromptDTO> prompts = promptService.getAllPromptsPaginated(pageable);
        return ResponseEntity.ok(prompts);
    }

    @Operation(summary = "Update a Prompt", description = "Updates an existing Prompt by ID")
    @ApiResponses(value={
            @ApiResponse(responseCode="200" ,description = "Prompt updated successfully",content=@Content(mediaType = "Application/json", schema=@Schema(implementation=PromptDTO.class))),
            @ApiResponse(responseCode="400",description="Invalid input",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode="404",description="Prompt not found",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode="500",description="Internal server error",content = @Content(schema=@Schema(type="object")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PromptDTO> updatePrompt(@PathVariable UUID id, @RequestBody PromptDTO promptDTO) {
        if (!promptService.getPromptById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        promptDTO = promptService.updatePrompt(id, promptDTO);
        return ResponseEntity.ok(promptDTO);
    }


    @Operation(summary = "Delete a Prompt", description = "Deletes a Prompt by ID")
    @ApiResponses(value={
            @ApiResponse(responseCode="200" ,description = "Prompt deleted successfully",content=@Content( schema=@Schema(type = "object"))),
            @ApiResponse(responseCode="204",description="Prompt deleted successfully without prompt returned ",content=@Content( schema=@Schema(type = "object"))),
            @ApiResponse(responseCode="400",description="Invalid input",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode="404",description="Prompt not found",content = @Content(schema=@Schema(type="object"))),
            @ApiResponse(responseCode="500",description="Internal server error",content = @Content(schema=@Schema(type="object")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrompt(@PathVariable UUID id) {
        if (!promptService.getPromptById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        promptService.deletePrompt(id);
        return ResponseEntity.noContent().build();
    }
}