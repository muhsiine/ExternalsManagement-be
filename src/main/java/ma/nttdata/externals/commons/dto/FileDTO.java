package ma.nttdata.externals.commons.dto;

import ma.nttdata.externals.commons.constants.MimeType;

public record FileDTO(String text, String b64EFile, MimeType mimeType, String schema) {

    // Ce constructeur intercepte la création pour corriger le mimeType s'il est null
    public FileDTO {
        if (mimeType == null && b64EFile != null) {
            // On essaie de détecter le type, sinon TEXT_PLAIN par défaut
            MimeType detected = MimeType.fromBase64(b64EFile);
            mimeType = (detected != null) ? detected : MimeType.TEXT_PLAIN;
        }
    }
}