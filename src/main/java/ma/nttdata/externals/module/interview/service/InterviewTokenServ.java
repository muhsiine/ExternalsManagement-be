package ma.nttdata.externals.module.interview.service;

import io.jsonwebtoken.Claims;
import ma.nttdata.externals.module.interview.dto.InterviewDTO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public interface InterviewTokenServ {

    String generateToken(LocalDateTime scheduledAt, UUID interviewId);

    boolean validateToken(String token);

    boolean isTokenExpired(String token);

    Date getIssuedAt(String token);

    Date extractExpiration(String token);

    Claims extractClaims(String token);

    UUID extractInterviewId(String token);

    boolean isValidAndNotExpired(String token);

    UUID getInterviewIdFromValidToken(String token);

    InterviewDTO getInterviewByValidToken(String token);

}