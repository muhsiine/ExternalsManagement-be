package ma.nttdata.externals.module.interview.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import ma.nttdata.externals.module.interview.dto.InterviewDTO;
import ma.nttdata.externals.module.interview.service.InterviewServ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewTokenServImplTest {

    private InterviewTokenServImpl tokenService;

    @Mock
    private InterviewServ interviewServ;

    private String testSecretKey;
    private long testExpirationMillis;
    private UUID testInterviewId;
    private InterviewDTO testInterviewDTO;

    @BeforeEach
    void setUp() {
        tokenService = new InterviewTokenServImpl(interviewServ);

        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        testSecretKey = Base64.getEncoder().encodeToString(key.getEncoded());
        testExpirationMillis = 3600000; // 1 hour
        testInterviewId = UUID.randomUUID();

        // Create test InterviewDTO - you'll need to adjust this based on your actual InterviewDTO constructor
        // For now, we'll mock it in individual tests instead of creating a real instance
        testInterviewDTO = mock(InterviewDTO.class);

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

        // JWT stores timestamps with second-level precision, so we need to account for this truncation
        // Calculate expected expiration time and truncate to seconds
        long expectedExpirationMillis = scheduledAt.atZone(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli() + testExpirationMillis;
        long expectedExpirationSeconds = expectedExpirationMillis / 1000;
        long actualExpirationSeconds = expiration.getTime() / 1000;

        assertEquals(expectedExpirationSeconds, actualExpirationSeconds);
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
    void extractInterviewId_WithInvalidUuidInToken_ShouldThrowException() {
        // Create a token with manually crafted claims containing invalid UUID
        Date now = new Date();
        Date expiry = new Date(now.getTime() + testExpirationMillis);

        String tokenWithInvalidUuid = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setId(UUID.randomUUID().toString())
                .claim("interviewId", "invalid-uuid-format") // Invalid UUID format
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(JwtException.class, () -> tokenService.extractInterviewId(tokenWithInvalidUuid));
    }

    @Test
    void extractInterviewId_WithTokenMissingInterviewIdClaim_ShouldThrowException() {
        // Create a token without the interviewId claim
        Date now = new Date();
        Date expiry = new Date(now.getTime() + testExpirationMillis);

        String tokenWithoutInterviewId = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setId(UUID.randomUUID().toString())
                // Note: No interviewId claim added here
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(JwtException.class, () -> tokenService.extractInterviewId(tokenWithoutInterviewId));
    }

    // NEW TESTS FOR THE NEW METHODS

    @Test
    void isValidAndNotExpired_WithValidToken_ShouldReturnTrue() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusHours(1);
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        boolean result = tokenService.isValidAndNotExpired(token);

        assertTrue(result);
    }

    @Test
    void isValidAndNotExpired_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.jwt.token";

        boolean result = tokenService.isValidAndNotExpired(invalidToken);

        assertFalse(result);
    }

    @Test
    void isValidAndNotExpired_WithExpiredToken_ShouldReturnFalse() {
        // Set a very short expiration time
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMillis", 1L);
        LocalDateTime scheduledAt = LocalDateTime.now();
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        try {
            Thread.sleep(10); // Wait for token to expire
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean result = tokenService.isValidAndNotExpired(token);

        assertFalse(result);
    }

    @Test
    void isValidAndNotExpired_WithNullToken_ShouldReturnFalse() {
        boolean result = tokenService.isValidAndNotExpired(null);

        assertFalse(result);
    }

    @Test
    void getInterviewIdFromValidToken_WithValidToken_ShouldReturnInterviewId() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusHours(1);
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        UUID result = tokenService.getInterviewIdFromValidToken(token);

        assertEquals(testInterviewId, result);
    }

    @Test
    void getInterviewIdFromValidToken_WithInvalidToken_ShouldThrowUnauthorized() {
        String invalidToken = "invalid.jwt.token";

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tokenService.getInterviewIdFromValidToken(invalidToken)
        );

        assertEquals(401, exception.getStatusCode().value());
        assertEquals("Invalid token", exception.getReason());
    }

    @Test
    void getInterviewIdFromValidToken_WithExpiredToken_ShouldThrowUnauthorized() {
        // Create an expired token manually with proper signing
        Date now = new Date();
        Date pastDate = new Date(now.getTime() - 3600000L); // 1 hour ago
        Date expiredDate = new Date(pastDate.getTime() + 1000L); // Expired 59 minutes ago

        String expiredToken = Jwts.builder()
                .setIssuedAt(pastDate)
                .setExpiration(expiredDate)
                .setId(UUID.randomUUID().toString())
                .claim("interviewId", testInterviewId.toString())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tokenService.getInterviewIdFromValidToken(expiredToken)
        );

        assertEquals(401, exception.getStatusCode().value());
        assertEquals("Invalid token", exception.getReason());
    }

    @Test
    void getInterviewIdFromValidToken_WithTokenMissingInterviewId_ShouldThrowBadRequest() {
        // Create a token without the interviewId claim
        Date now = new Date();
        Date expiry = new Date(now.getTime() + testExpirationMillis);

        String tokenWithoutInterviewId = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setId(UUID.randomUUID().toString())
                // Note: No interviewId claim added here
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tokenService.getInterviewIdFromValidToken(tokenWithoutInterviewId)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals("Unable to extract interview ID from token", exception.getReason());
    }

    @Test
    void getInterviewByValidToken_WithValidToken_ShouldReturnInterview() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusHours(1);
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        when(interviewServ.getInterviewById(testInterviewId)).thenReturn(testInterviewDTO);

        InterviewDTO result = tokenService.getInterviewByValidToken(token);

        assertEquals(testInterviewDTO, result);
        verify(interviewServ).getInterviewById(testInterviewId);
    }

    @Test
    void getInterviewByValidToken_WithInvalidToken_ShouldThrowUnauthorized() {
        String invalidToken = "invalid.jwt.token";

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tokenService.getInterviewByValidToken(invalidToken)
        );

        assertEquals(401, exception.getStatusCode().value());
        assertEquals("Invalid token", exception.getReason());
        verify(interviewServ, never()).getInterviewById(any());
    }

    @Test
    void getInterviewByValidToken_WithExpiredToken_ShouldThrowUnauthorized() {
        // Create an expired token manually with proper signing
        Date now = new Date();
        Date pastDate = new Date(now.getTime() - 3600000L); // 1 hour ago
        Date expiredDate = new Date(pastDate.getTime() + 1000L); // Expired 59 minutes ago

        String expiredToken = Jwts.builder()
                .setIssuedAt(pastDate)
                .setExpiration(expiredDate)
                .setId(UUID.randomUUID().toString())
                .claim("interviewId", testInterviewId.toString())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tokenService.getInterviewByValidToken(expiredToken)
        );

        assertEquals(401, exception.getStatusCode().value());
        assertEquals("Invalid token", exception.getReason());
        verify(interviewServ, never()).getInterviewById(any());
    }

    @Test
    void getInterviewByValidToken_WithInterviewNotFound_ShouldThrowNotFound() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusHours(1);
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        // Mock the service to throw a specific exception that should result in NOT_FOUND
        when(interviewServ.getInterviewById(testInterviewId)).thenThrow(new RuntimeException("Interview not found"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tokenService.getInterviewByValidToken(token)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Interview not found", exception.getReason());
        verify(interviewServ).getInterviewById(testInterviewId);
    }

    @Test
    void getInterviewByValidToken_WithInterviewServiceException_ShouldThrowNotFound() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusHours(1);
        String token = tokenService.generateToken(scheduledAt, testInterviewId);

        // Mock the service to throw IllegalArgumentException which extends RuntimeException
        // According to the implementation, RuntimeException is caught and results in NOT_FOUND
        when(interviewServ.getInterviewById(testInterviewId)).thenThrow(new IllegalArgumentException("Invalid argument"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> tokenService.getInterviewByValidToken(token)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Interview not found", exception.getReason());
        verify(interviewServ).getInterviewById(testInterviewId);
    }

    // EXISTING TESTS CONTINUE...

    @Test
    void tokenWorkflow_GenerateValidateAndExtract_ShouldWorkCorrectly() {
        long beforeGeneration = System.currentTimeMillis() / 1000;
        LocalDateTime date = LocalDateTime.now();
        String token = tokenService.generateToken(date, testInterviewId);

        assertTrue(tokenService.validateToken(token));
        assertFalse(tokenService.isTokenExpired(token));
        assertTrue(tokenService.isValidAndNotExpired(token));

        Date issuedAt = tokenService.getIssuedAt(token);
        Date expiration = tokenService.extractExpiration(token);
        Claims claims = tokenService.extractClaims(token);
        UUID extractedInterviewId = tokenService.extractInterviewId(token);
        UUID extractedInterviewIdFromValidToken = tokenService.getInterviewIdFromValidToken(token);

        assertNotNull(issuedAt);
        long issuedAtSeconds = issuedAt.getTime() / 1000;
        assertNotNull(expiration);
        assertNotNull(claims);
        assertEquals(testInterviewId, extractedInterviewId);
        assertEquals(testInterviewId, extractedInterviewIdFromValidToken);
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

        // JWT stores timestamps with second-level precision, so we need to account for this truncation
        long expectedExpirationMillis = scheduledAt.atZone(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli() + customExpiration;
        long expectedExpirationSeconds = expectedExpirationMillis / 1000;
        long actualExpirationSeconds = expiration.getTime() / 1000;

        assertEquals(expectedExpirationSeconds, actualExpirationSeconds);
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
        assertTrue(tokenService.isValidAndNotExpired(token1));
        assertTrue(tokenService.isValidAndNotExpired(token2));

        // Verify different interview IDs are correctly embedded
        assertEquals(interviewId1, tokenService.extractInterviewId(token1));
        assertEquals(interviewId2, tokenService.extractInterviewId(token2));
        assertEquals(interviewId1, tokenService.getInterviewIdFromValidToken(token1));
        assertEquals(interviewId2, tokenService.getInterviewIdFromValidToken(token2));
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
        assertTrue(tokenService.isValidAndNotExpired(token1));
        assertTrue(tokenService.isValidAndNotExpired(token2));

        // But they should have the same interview ID
        assertEquals(testInterviewId, tokenService.extractInterviewId(token1));
        assertEquals(testInterviewId, tokenService.extractInterviewId(token2));
        assertEquals(testInterviewId, tokenService.getInterviewIdFromValidToken(token1));
        assertEquals(testInterviewId, tokenService.getInterviewIdFromValidToken(token2));
    }

    /**
     * Helper method to create signing key for manual token creation in tests
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(testSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}