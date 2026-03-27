package ma.nttdata.externals.commons.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmailContentBuilderTest {

    private EmailContentBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new EmailContentBuilder();

        // Inject template path manually
        ReflectionTestUtils.setField(
                builder,
                "InterviewInvitationEmailTemplate",
                "test-template.txt"
        );
    }

    @Test
    void shouldBuildInterviewEmailSuccessfully() {
        // Arrange
        String fullName = "John Doe";
        String offerTitle = "Java Developer";
        String link = "http://interview-link.com";
        LocalDateTime date = LocalDateTime.of(2026, 3, 20, 14, 30);

        // Act
        String result = builder.buildInterviewEmail(fullName, offerTitle, link, date);

        // Assert
        System.out.println(result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenTemplateNotFound() {
        // Arrange
        ReflectionTestUtils.setField(
                builder,
                "InterviewInvitationEmailTemplate",
                "does-not-exist.txt"
        );
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> builder.buildInterviewEmail(
                        "John",
                        "Offer",
                        "link",
                        LocalDateTime.now()
                )
        );

        assertEquals("Failed to load email template", exception.getMessage());
        assertNotNull(exception.getCause());
    }
}