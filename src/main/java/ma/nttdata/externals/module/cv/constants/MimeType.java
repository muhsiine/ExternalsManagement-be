package ma.nttdata.externals.module.cv.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum MimeType {

    // Text Types
    TEXT_PLAIN("text/plain", "txt"),
    TEXT_HTML("text/html", "html", "htm"),
    TEXT_CSS("text/css", "css"),
    TEXT_JAVASCRIPT("text/javascript", "js"),
    TEXT_CSV("text/csv", "csv"),
    TEXT_XML("text/xml", "xml"),

    // Image Types
    IMAGE_JPEG("image/jpeg", "jpeg", "jpg"),
    IMAGE_PNG("image/png", "png"),
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_BMP("image/bmp", "bmp"),
    IMAGE_WEBP("image/webp", "webp"),
    IMAGE_SVG("image/svg+xml", "svg"),

    // Audio Types
    AUDIO_MPEG("audio/mpeg", "mp3"),
    AUDIO_WAV("audio/wav", "wav"),
    AUDIO_OGG("audio/ogg", "ogg"),
    AUDIO_AAC("audio/aac", "aac"),

    // Video Types
    VIDEO_MP4("video/mp4", "mp4"),
    VIDEO_MPEG("video/mpeg", "mpeg", "mpg"),
    VIDEO_WEBM("video/webm", "webm"),
    VIDEO_OGG("video/ogg", "ogv"),

    // Application Types
    APPLICATION_JSON("application/json", "json"),
    APPLICATION_PDF("application/pdf", "pdf"),
    APPLICATION_ZIP("application/zip", "zip"),
    APPLICATION_GZIP("application/gzip", "gz"),
    APPLICATION_OCTET_STREAM("application/octet-stream"), // Generic binary data
    APPLICATION_MSWORD("application/msword", "doc"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_SHEET("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_PRESENTATION("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),
    APPLICATION_X_SHOCKWAVE_FLASH("application/x-shockwave-flash", "swf"),
    APPLICATION_JAVA_ARCHIVE("application/java-archive", "jar"),
    APPLICATION_POSTSCRIPT("application/postscript", "ps"),


    // Font Types
    FONT_WOFF("font/woff", "woff"),
    FONT_WOFF2("font/woff2", "woff2"),
    FONT_TTF("font/ttf", "ttf"),
    FONT_OTF("font/otf", "otf");

    private final String mimeString;
    private final List<String> extensions;


    MimeType(String mimeString, String... extensions) {
        this.mimeString = mimeString;
        this.extensions = Collections.unmodifiableList(Arrays.asList(extensions));
    }

    public String getMimeString() {
        return mimeString;
    }

    public List<String> getExtensions() {
        return extensions;
    }


    public static Optional<MimeType> fromString(String mimeString) {
        for (MimeType mimeType : values()) {
            if (mimeType.mimeString.equalsIgnoreCase(mimeString)) {
                return Optional.of(mimeType);
            }
        }
        return Optional.empty();
    }

    public static Optional<MimeType> fromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return Optional.empty();
        }

        String ext = extension.startsWith(".") ? extension.substring(1).toLowerCase() : extension.toLowerCase();

        for (MimeType mimeType : values()) {
            if (mimeType.extensions.contains(ext)) {
                return Optional.of(mimeType);
            }
        }
        return Optional.empty();
    }

    public boolean matchesExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        String ext = extension.startsWith(".") ? extension.substring(1).toLowerCase() : extension.toLowerCase();
        return extensions.contains(ext);
    }


}
