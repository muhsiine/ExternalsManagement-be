package ma.nttdata.externals.commons.services.impl;

import ma.nttdata.externals.commons.dto.FileDTO;
import ma.nttdata.externals.commons.constants.MimeType;
import ma.nttdata.externals.commons.services.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;  // ✅ générique
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import java.util.Base64;

@Service
public class GoogleChatService implements ChatService {

    private final ChatClient client;

    public GoogleChatService(ChatClient.Builder chatClientBuilder) {
        this.client = chatClientBuilder.build();
    }

    @Override
    public String call(String text) {
        return client.prompt()
                .user(text)
                .call()
                .content();
    }

    @Override
    public String call(String text, byte[] fileBytes, MimeType mimeType) {
        Resource resource = new ByteArrayResource(fileBytes);
        org.springframework.util.MimeType springMime =
                MimeTypeUtils.parseMimeType(mimeType.getMimeString());

        return client.prompt()
                .user(u -> u.text(text).media(springMime, resource))
                .call()
                .content();
    }

    @Override
    public String call(String text, String base64EncodedFile, MimeType mimeType) {
        byte[] bytes = Base64.getDecoder().decode(base64EncodedFile);
        return call(text, bytes, mimeType);
    }

    @Override
    public String getJson(FileDTO fileDTO) {
        return getJson(fileDTO.text(), fileDTO.b64EFile(), fileDTO.mimeType(), fileDTO.schema());
    }

    @Override
    public String getJson(String text, String base64EncodedFile, MimeType mimeType, String schema) {
        byte[] fileBytes = Base64.getDecoder().decode(base64EncodedFile);
        Resource resource = new ByteArrayResource(fileBytes);
        org.springframework.util.MimeType springMime =
                MimeTypeUtils.parseMimeType(mimeType.getMimeString());

        // ✅ Options génériques Spring AI — marche avec tous les modèles
        ChatOptions options = ChatOptions.builder()
                .temperature(0.2)
                .topP(0.6)
                .build();

        return client.prompt()
                .options(options)
                .user(u -> u.text(text).media(springMime, resource))
                .call()
                .content();
    }
}
