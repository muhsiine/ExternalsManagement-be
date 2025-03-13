package ma.nttdata.externals.module.ai.service;

import reactor.core.publisher.Flux;

public interface LlmService {
    String call(String text);
    String call(String text, byte[] fileBytes, String mimeType);

    String getJson(String text, String base64EncodedFile, String mimeType, String schema);
    String getJson2(String text, String base64EncodedFile, String mimeType, String schema);

    Flux<String> callStream(String text);
}
