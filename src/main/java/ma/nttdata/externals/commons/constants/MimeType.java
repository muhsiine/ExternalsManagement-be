package ma.nttdata.externals.commons.constants;

import org.apache.tika.Tika;
import java.util.*;

public enum MimeType {

    // Text Types
    TEXT_PLAIN("text/plain", Arrays.asList("txt")),
    TEXT_HTML("text/html", Arrays.asList("html", "htm")),
    TEXT_CSS("text/css", Arrays.asList("css")),
    TEXT_JAVASCRIPT("text/javascript", Arrays.asList("js")),
    TEXT_CSV("text/csv", Arrays.asList("csv")),
    TEXT_XML("text/xml", Arrays.asList("xml")),

    // Image Types
    IMAGE_JPEG("image/jpeg", Arrays.asList("jpeg", "jpg")),
    IMAGE_PNG("image/png", Arrays.asList("png")),
    IMAGE_GIF("image/gif", Arrays.asList("gif")),
    IMAGE_BMP("image/bmp", Arrays.asList("bmp")),
    IMAGE_WEBP("image/webp", Arrays.asList("webp")),
    IMAGE_SVG("image/svg+xml", Arrays.asList("svg")),

    // Application Types
    APPLICATION_JSON("application/json", Arrays.asList("json")),
    APPLICATION_PDF("application/pdf", Arrays.asList("pdf")),
    APPLICATION_MSWORD("application/msword", Arrays.asList("doc")),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            Arrays.asList("docx")
    ),
    APPLICATION_OCTET_STREAM("application/octet-stream", Collections.emptyList());

    private final String mimeString;
    private final List<String> extensions;

    // Constructeur
    MimeType(String mimeString, List<String> extensions) {
        this.mimeString = mimeString;
        this.extensions = extensions;
    }

    public String getMimeString() {
        return mimeString;
    }

    public boolean matchesExtension(String extension) {
        if (extension == null) return false;
        String ext = extension.startsWith(".") ? extension.substring(1) : extension;
        return extensions.contains(ext.toLowerCase());
    }

    // --- Méthodes Statiques (Équivalent du Companion Object) ---

    public static MimeType fromString(String mimeString) {
        return Arrays.stream(MimeType.values())
                .filter(type -> type.mimeString.equalsIgnoreCase(mimeString))
                .findFirst()
                .orElse(null);
    }

    public static MimeType fromExtension(String extension) {
        if (extension == null || extension.isEmpty()) return null;
        String ext = extension.startsWith(".") ? extension.substring(1) : extension;
        String finalExt = ext.toLowerCase();

        return Arrays.stream(MimeType.values())
                .filter(type -> type.extensions.contains(finalExt))
                .findFirst()
                .orElse(null);
    }

    public static MimeType fromBase64(String b64) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(b64);
            Tika tika = new Tika();
            String mimeTypeString = tika.detect(decodedBytes);
            return fromString(mimeTypeString);
        } catch (Exception e) {
            return null;
        }
    }
}