package ma.nttdata.externals.module.ai.service.impl;

import com.google.genai.Client;
import com.google.genai.types.Blob;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import ma.nttdata.externals.module.ai.service.LlmService;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GoogleLlmService implements LlmService {
    private String url = "https://generativelanguage.googleapis.com/v1/models/";
    private final Client client;
    private String apiKey;
    private String model;
    public GoogleLlmService(@Value("${llm.api.google.key}") String apiKey,
                             @Value("${llm.api.google.model}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    @Override
    public String call(String text) {

        Part textPart = Part.builder()
                .text(text)
                .build();
        Content content = Content.builder()
                .parts(List.of(textPart))
                .build();

        return getText(content);
    }

    @Override
    public String call(String text, byte[] fileBytes, String mimeType) {

        Blob pdfBlob = Blob.builder()
                .data(Base64.getEncoder().encodeToString(fileBytes))
                .mimeType(mimeType)
                .build();

        Part textPart = Part.builder()
                .text(text)
                .build();

        Part mediaPart = Part.builder()
                .inlineData(pdfBlob)
                .build();


        Content content = Content.builder()
                .parts(List.of(textPart, mediaPart))
                .build();

        return getText(content);

    }

    private String getText(Content content) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    model, // Or correct model name
                    content,
                    null);
            return response.text();
        }catch (IOException | HttpException e) {
            throw new RuntimeException(e);
        }
    }

    public Flux<String> callStream(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", Map.of("role", "user", "parts", List.of(Map.of("text", prompt)))
        );
        var uri = url + model + ":streamGenerateContent?alt=sse";
        var webClient = WebClient.builder()
                .baseUrl(uri)
                .build();
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(GenerateContentResponse.class)
                .flatMap(g -> Flux.fromIterable(g.candidates().get()))
                .flatMap(c -> Flux.fromIterable(c.content().get().parts().get()))
                .map(part -> part.text().get());


    }
}
