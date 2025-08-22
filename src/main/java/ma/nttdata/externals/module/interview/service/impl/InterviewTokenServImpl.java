package ma.nttdata.externals.module.interview.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import ma.nttdata.externals.module.interview.service.InterviewTokenServ;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class InterviewTokenServImpl implements InterviewTokenServ {

    @Value("${interview.token.secret}")
    private String secretKey;

    @Value("${interview.token.expirationMillis:86400000}")
    private long tokenExpirationMillis;

    private static final String INTERVIEW_ID_CLAIM = "interviewId";


    @Override
    public String generateToken(LocalDateTime scheduledAt, UUID interviewId) {
        Date now = new Date();
        Instant instant = scheduledAt.atZone(ZoneId.systemDefault()).toInstant();
        Date expiryDate = Date.from(instant.plusMillis(tokenExpirationMillis));

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setId(UUID.randomUUID().toString())
                .claim(INTERVIEW_ID_CLAIM, interviewId.toString())  // Use the interviewId parameter
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return true;
        }
    }

    @Override
    public Date getIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public UUID extractInterviewId(String token) {
        try {
            Claims claims = extractClaims(token);
            String interviewIdStr = claims.get(INTERVIEW_ID_CLAIM, String.class);
            if (interviewIdStr == null) {
                throw new JwtException("Interview ID not found in token");
            }
            return UUID.fromString(interviewIdStr);
        } catch (Exception e) {
            throw new JwtException("Invalid token or unable to extract interview ID", e);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractClaims(token);
        return resolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}