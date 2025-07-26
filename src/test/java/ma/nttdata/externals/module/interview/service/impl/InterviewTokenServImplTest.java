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

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InterviewTokenServImplTest {

    private InterviewTokenServImpl tokenService;
    private String testSecretKey;
    private long testExpirationMillis;

    @BeforeEach
    void setUp() {
        tokenService = new InterviewTokenServImpl();

        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        testSecretKey = Base64.getEncoder().encodeToString(key.getEncoded());
        testExpirationMillis = 3600000;

        ReflectionTestUtils.setField(tokenService, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMillis", testExpirationMillis);
    }

    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        String token = tokenService.generateToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = tokenService.generateToken();

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
        String validToken = tokenService.generateToken();
        String tamperedToken = validToken.substring(0, validToken.length() - 1) + "X";

        boolean isValid = tokenService.validateToken(tamperedToken);

        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_WithFreshToken_ShouldReturnFalse() {
        String token = tokenService.generateToken();

        boolean isExpired = tokenService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_WithExpiredToken_ShouldReturnTrue() {
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMillis", 1L);
        String token = tokenService.generateToken();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isExpired = tokenService.isTokenExpired(token);

        assertTrue(isExpired);
    }

    @Test
    void getIssuedAt_ShouldReturnCorrectIssuedAtDate() {
        //JWT libraries standardly truncate timestamps to seconds precision, removing milliseconds
        // Convert to seconds because JWT 'iat' (issued at) is stored with second-level precision,
        // and Date.getTime() returns milliseconds. Comparing in milliseconds would be inaccurate.
        long before = System.currentTimeMillis() / 1000;
        String token = tokenService.generateToken();
        long after = System.currentTimeMillis() / 1000;

        Date issuedAt = tokenService.getIssuedAt(token);

        assertNotNull(issuedAt);
        long issuedAtSeconds = issuedAt.getTime() / 1000;

        assertTrue(issuedAtSeconds >= before, "issuedAt should be after or equal to before");
        assertTrue(issuedAtSeconds <= after, "issuedAt should be before or equal to after");
    }



    @Test
    void extractExpiration_ShouldReturnCorrectExpirationDate() {
        String token = tokenService.generateToken();
        Date issuedAt = tokenService.getIssuedAt(token);

        Date expiration = tokenService.extractExpiration(token);

        assertNotNull(expiration);
        assertEquals(issuedAt.getTime() + testExpirationMillis, expiration.getTime());
    }

    @Test
    void extractClaims_ShouldReturnValidClaims() {
        String token = tokenService.generateToken();

        Claims claims = tokenService.extractClaims(token);

        assertNotNull(claims);
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void extractClaims_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.jwt.token";

        assertThrows(JwtException.class, () -> tokenService.extractClaims(invalidToken));
    }

    @Test
    void tokenWorkflow_GenerateValidateAndExtract_ShouldWorkCorrectly() {
        long beforeGeneration = System.currentTimeMillis() / 1000;

        String token = tokenService.generateToken();

        assertTrue(tokenService.validateToken(token));

        assertFalse(tokenService.isTokenExpired(token));

        Date issuedAt = tokenService.getIssuedAt(token);
        Date expiration = tokenService.extractExpiration(token);
        Claims claims = tokenService.extractClaims(token);

        assertNotNull(issuedAt);
        long issuedAtSeconds = issuedAt.getTime() / 1000;
        assertNotNull(expiration);
        assertNotNull(claims);
        assertEquals(issuedAt, claims.getIssuedAt());
        assertEquals(expiration, claims.getExpiration());
        assertTrue(expiration.after(issuedAt));
        assertTrue(issuedAtSeconds >= beforeGeneration);
    }

    @Test
    void generateToken_WithDifferentExpirationTime_ShouldRespectConfiguration() {
        long customExpiration = 7200000;
        ReflectionTestUtils.setField(tokenService, "tokenExpirationMillis", customExpiration);

        String token = tokenService.generateToken();

        Date issuedAt = tokenService.getIssuedAt(token);
        Date expiration = tokenService.extractExpiration(token);

        assertEquals(issuedAt.getTime() + customExpiration, expiration.getTime());
    }

    @Test
    void multipleTokenGeneration_ShouldProduceDifferentTokens() {
        String token1 = tokenService.generateToken();
        String token2 = tokenService.generateToken();

        assertNotEquals(token1, token2);
        assertTrue(tokenService.validateToken(token1));
        assertTrue(tokenService.validateToken(token2));
    }
}