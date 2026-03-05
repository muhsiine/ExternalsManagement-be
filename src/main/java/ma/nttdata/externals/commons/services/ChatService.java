package ma.nttdata.externals.commons.services;

import ma.nttdata.externals.commons.constants.MimeType;
import ma.nttdata.externals.commons.dto.FileDTO;

public interface ChatService {

    String call(String text);

    String call(String text, byte[] fileBytes, MimeType mimeType);

    String call(String text, String base64EncodedFile, MimeType mimeType);

    String getJson(String text, String base64EncodedFile, MimeType mimeType, String schema);

    String getJson(FileDTO fileDTO);
}