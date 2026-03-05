package ma.nttdata.externals.commons.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.nttdata.externals.commons.constants.MimeType;

/**
 * DTO d'entrée — reçoit la requête brute du client (HTTP @RequestBody)
 * Pas de logique métier ici, uniquement validations input.
 */
public record FileRequestDTO(

        @NotBlank(message = "Le texte / prompt ne peut pas être vide")
        String text,

        @NotBlank(message = "Le fichier Base64 est obligatoire")
        String b64EFile,

        // mimeType peut être null → sera calculé dans FileDTO
        MimeType mimeType,

        @NotNull(message = "Le schema JSON est obligatoire")
        String schema

) {}