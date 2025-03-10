package ma.nttdata.externals.module.ai.service;

import reactor.core.publisher.Flux;

public interface LlmService {
    String call(String text);
    String call(String text, byte[] fileBytes, String mimeType);
    Flux<String> callStream(String text);
}
