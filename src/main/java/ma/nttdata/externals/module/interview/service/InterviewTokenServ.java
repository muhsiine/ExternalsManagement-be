package ma.nttdata.externals.module.interview.service;

import io.jsonwebtoken.Claims;

import java.time.LocalDateTime;
import java.util.Date;

public interface InterviewTokenServ {

    String generateToken(LocalDateTime scheduledAt);

    boolean validateToken(String token);

    boolean isTokenExpired(String token);

    Date getIssuedAt(String token);

    Date extractExpiration(String token);

    Claims extractClaims(String token);
}
