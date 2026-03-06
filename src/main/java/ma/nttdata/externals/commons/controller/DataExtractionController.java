package ma.nttdata.externals.commons.controller;

import jakarta.validation.Valid;
import ma.nttdata.externals.commons.dto.FileDTO;
import ma.nttdata.externals.commons.dto.FileRequestDTO;
import ma.nttdata.externals.commons.services.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/api/v1/extract")
public class DataExtractionController {

    private final ChatService chatService;

    public DataExtractionController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String extractData(@Valid @RequestBody FileRequestDTO request) {

        // Conversion FileRequestDTO (input client) → FileDTO (objet interne)
        // La logique mimeType null → auto-détection se fait dans le compact constructor de FileDTO
        FileDTO fileDTO = new FileDTO(
                request.text(),
                request.b64EFile(),
                request.mimeType(),  // peut être null ici → FileDTO le calcule automatiquement
                request.schema()
        );

        return chatService.getJson(fileDTO);
    }
}