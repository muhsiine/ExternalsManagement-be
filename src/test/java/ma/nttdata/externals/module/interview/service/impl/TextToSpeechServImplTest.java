package ma.nttdata.externals.module.interview.service.impl;

import ma.nttdata.externals.commons.config.TextToSpeechPropertiesConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.audio.tts.Speech;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.elevenlabs.ElevenLabsTextToSpeechModel;
import org.springframework.ai.elevenlabs.api.ElevenLabsApi;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TextToSpeechServImplTest {

    @Mock
    private TextToSpeechPropertiesConfig propertiesConfig;

    @Mock
    private ElevenLabsApi elevenLabsApi;

    @Mock
    private ElevenLabsTextToSpeechModel textToSpeechModel;

    @Mock
    private TextToSpeechResponse textToSpeechResponse;

    @Mock
    private Speech speech;

    private TextToSpeechServImpl ttsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(propertiesConfig.getModelId()).thenReturn("model123");
        when(propertiesConfig.getVoiceId()).thenReturn("voice123");
        when(propertiesConfig.getStability()).thenReturn(0.5);
        when(propertiesConfig.getSimilarityBoost()).thenReturn(0.7);
        when(propertiesConfig.getStyle()).thenReturn(0.4);
        when(propertiesConfig.isUseSpeakerBoost()).thenReturn(false);
        when(propertiesConfig.getSpeed()).thenReturn(1.0);

        ttsService = new TextToSpeechServImpl(propertiesConfig, elevenLabsApi);
    }

    @Test
    void speak_ShouldReturnAudioBytes_WhenValidTextProvided() {
        String inputText = "Hello, this is a test message.";
        byte[] expectedAudioBytes = "mock audio data".getBytes();

        when(propertiesConfig.getModelId()).thenReturn("eleven_multilingual_v2");
        when(propertiesConfig.getVoiceId()).thenReturn("21m00Tcm4TlvDq8ikWAM");
        when(propertiesConfig.getStability()).thenReturn(0.5);
        when(propertiesConfig.getSimilarityBoost()).thenReturn(0.8);
        when(propertiesConfig.getStyle()).thenReturn(0.0);
        when(propertiesConfig.isUseSpeakerBoost()).thenReturn(true);
        when(propertiesConfig.getSpeed()).thenReturn(1.0);

        when(speech.getOutput()).thenReturn(expectedAudioBytes);
        when(textToSpeechResponse.getResult()).thenReturn(speech);


        try (MockedStatic<ElevenLabsTextToSpeechModel> mockedModel = mockStatic(ElevenLabsTextToSpeechModel.class)) {
            ElevenLabsTextToSpeechModel.Builder mockBuilder = mock(ElevenLabsTextToSpeechModel.Builder.class);

            mockedModel.when(ElevenLabsTextToSpeechModel::builder).thenReturn(mockBuilder);
            when(mockBuilder.elevenLabsApi(any())).thenReturn(mockBuilder);
            when(mockBuilder.defaultOptions(any())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(textToSpeechModel);

            when(textToSpeechModel.call(any(TextToSpeechPrompt.class))).thenReturn(textToSpeechResponse);

            byte[] result = ttsService.speak(inputText);

            assertNotNull(result);
            assertArrayEquals(expectedAudioBytes, result);

            verify(textToSpeechModel).call(any(TextToSpeechPrompt.class));
            verify(textToSpeechResponse).getResult();
        }
    }
}
