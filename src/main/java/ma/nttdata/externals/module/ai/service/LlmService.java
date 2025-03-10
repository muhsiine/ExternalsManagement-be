package ma.nttdata.externals.module.ai.service;

public interface LlmService {
    String call(String text);
    String call(String text, byte[] fileBytes, String mimeType);
}
