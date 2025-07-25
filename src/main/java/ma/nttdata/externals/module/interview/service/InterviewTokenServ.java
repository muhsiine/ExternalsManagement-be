package ma.nttdata.externals.module.interview.service;

import io.jsonwebtoken.Claims;

import java.util.Date;

public interface InterviewTokenServ {

    String generateToken();

    boolean validateToken(String token);

    boolean isTokenExpired(String token);

    Date getIssuedAt(String token);

    Date extractExpiration(String token);

    Claims extractClaims(String token);
}
