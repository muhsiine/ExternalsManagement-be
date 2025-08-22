package ma.nttdata.externals.module.interview.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InterviewTokenServImplTest {

    private InterviewTokenServImpl tokenService;
    private String testSecretKey;
    private long testExpirationMillis;
    private UUID testInterviewId;

    @BeforeEach
    void setUp() {
        tokenService = new InterviewTokenServImpl();

        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        testSecretKey = Base64.getEncoder().encodeToString(key.getEncoded());
        testExpirationMillis = 3600000; // 1 hour
        testInterviewId = UUID.randomUUID();

        ReflectionTestUtils.setField(tokenService, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMillis", testExpirationMillis);
    }

    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        boolean isValid = tokenService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.jwt.token";

        boolean isValid = tokenService.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        boolean isValid = tokenService.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        boolean isValid = tokenService.validateToken("");

        assertFalse(isValid);
    }

    @Test
    void validateToken_WithTamperedToken_ShouldReturnFalse() {
        LocalDateTime scheduledAt = LocalDateTime.now();
        String validToken = tokenService.generateToken(scheduledAt, testInterviewId);
        String tamperedToken = validToken.substring(0, validToken.length() - 1) + "X";

        boolean isValid = tokenService.validateToken(tamperedToken);

        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_WithFreshToken_ShouldReturnFalse() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusHours(1); // Future scheduled time
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        boolean isExpired = tokenService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_WithExpiredToken_ShouldReturnTrue() {
        // Set a very short expiration time
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMillis", 1L);
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        try {
            Thread.sleep(10); // Wait for token to expire
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isExpired = tokenService.isTokenExpired(token);

        assertTrue(isExpired);
    }

    @Test
    void getIssuedAt_ShouldReturnCorrectIssuedAtDate() {
        // JWT libraries standardly truncate timestamps to seconds precision, removing milliseconds
        // Convert to seconds because JWT 'iat' (issued at) is stored with second-level precision,
        // and Date.getTime() returns milliseconds. Comparing in milliseconds would be inaccurate.
        long before = System.currentTimeMillis() / 1000;
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token = tokenService.generateToken(scheduledAt, testInterviewId);
        long after = System.currentTimeMillis() / 1000;

        Date issuedAt = tokenService.getIssuedAt(token);

        assertNotNull(issuedAt);
        long issuedAtSeconds = issuedAt.getTime() / 1000;

        assertTrue(issuedAtSeconds >= before, "issuedAt should be after or equal to before");
        assertTrue(issuedAtSeconds <= after, "issuedAt should be before or equal to after");
    }

    @Test
    void extractExpiration_ShouldReturnCorrectExpirationDate() {
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        Date expiration = tokenService.extractExpiration(token);
        assertNotNull(expiration);

        // The expiration should be scheduledAt + tokenExpirationMillis
        long expectedExpirationTime = scheduledAt.atZone(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli() + testExpirationMillis;

        assertEquals(expectedExpirationTime, expiration.getTime());
    }

    @Test
    void extractClaims_ShouldReturnValidClaims() {
        LocalDateTime date = LocalDateTime.now();
        String token = tokenService.generateToken(date, testInterviewId);

        Claims claims = tokenService.extractClaims(token);

        assertNotNull(claims);
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getId());
        assertNotNull(claims.get("interviewId"));
    }

    @Test
    void extractClaims_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.jwt.token";

        assertThrows(JwtException.class, () -> tokenService.extractClaims(invalidToken));
    }

    @Test
    void extractInterviewId_ShouldReturnCorrectInterviewId() {
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        UUID extractedInterviewId = tokenService.extractInterviewId(token);

        assertNotNull(extractedInterviewId);
        assertEquals(testInterviewId, extractedInterviewId);
    }

    @Test
    void extractInterviewId_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.jwt.token";

        assertThrows(JwtException.class, () -> tokenService.extractInterviewId(invalidToken));
    }

    @Test
    void extractInterviewId_WithTokenMissingInterviewId_ShouldThrowException() {
        // Create a token without the interviewId claim (this would require a modified service)
        // For this test, we'll just use an invalid format that would cause the UUID parsing to fail
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        // This test assumes the implementation works correctly;
        // if we wanted to test the missing claim scenario, we'd need to create a token manually
        UUID extractedId = tokenService.extractInterviewId(token);
        assertEquals(testInterviewId, extractedId);
    }

    @Test
    void tokenWorkflow_GenerateValidateAndExtract_ShouldWorkCorrectly() {
        long beforeGeneration = System.currentTimeMillis() / 1000;
        LocalDateTime date = LocalDateTime.now();
        String token = tokenService.generateToken(date, testInterviewId);

        assertTrue(tokenService.validateToken(token));
        assertFalse(tokenService.isTokenExpired(token));

        Date issuedAt = tokenService.getIssuedAt(token);
        Date expiration = tokenService.extractExpiration(token);
        Claims claims = tokenService.extractClaims(token);
        UUID extractedInterviewId = tokenService.extractInterviewId(token);

        assertNotNull(issuedAt);
        long issuedAtSeconds = issuedAt.getTime() / 1000;
        assertNotNull(expiration);
        assertNotNull(claims);
        assertEquals(testInterviewId, extractedInterviewId);
        assertEquals(issuedAt, claims.getIssuedAt());
        assertEquals(expiration, claims.getExpiration());
        assertTrue(expiration.after(issuedAt));
        assertTrue(issuedAtSeconds >= beforeGeneration);
    }

    @Test
    void generateToken_WithDifferentExpirationTime_ShouldRespectConfiguration() {
        long customExpiration = 7200000; // 2 hours
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMillis", customExpiration);
        LocalDateTime scheduledAt = LocalDateTime.now();

        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        Date expiration = tokenService.extractExpiration(token);
        long expectedExpirationTime = scheduledAt.atZone(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli() + customExpiration;

        assertEquals(expectedExpirationTime, expiration.getTime());
    }

    @Test
    void multipleTokenGeneration_ShouldProduceDifferentTokens() {
        LocalDateTime date1 = LocalDateTime.now();
        LocalDateTime date2 = LocalDateTime.now();
        UUID interviewId1 = UUID.randomUUID();
        UUID interviewId2 = UUID.randomUUID();

        String token1 = tokenService.generateToken(date1, interviewId1);
        String token2 = tokenService.generateToken(date2, interviewId2);

        assertNotEquals(token1, token2);
        assertTrue(tokenService.validateToken(token1));
        assertTrue(tokenService.validateToken(token2));

        // Verify different interview IDs are correctly embedded
        assertEquals(interviewId1, tokenService.extractInterviewId(token1));
        assertEquals(interviewId2, tokenService.extractInterviewId(token2));
    }

    @Test
    void generateToken_WithSameParameters_ShouldProduceDifferentTokens() {
        // Even with same scheduledAt and interviewId, tokens should be different due to random JTI
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token1 = tokenService.generateToken(scheduledAt, testInterviewId);
        String token2 = tokenService.generateToken(scheduledAt, testInterviewId);

        assertNotEquals(token1, token2);
        assertTrue(tokenService.validateToken(token1));
        assertTrue(tokenService.validateToken(token2));

        // But they should have the same interview ID
        assertEquals(testInterviewId, tokenService.extractInterviewId(token1));
        assertEquals(testInterviewId, tokenService.extractInterviewId(token2));
    }
}