package com.example.Netflix.JWT;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.util.Date;

@Component
public class JwtTokenFactory {
    private final String secretKey = "BHJEFJKHVBEHKJVBFHJKVWEBVHBWJKERRVBHJERKWVBJHWEJKHBVHWRJBTVRJTWRFLPOFOTKGRTDWSLFVRWIOTBJROERBRNBFGBKMFDBFNGMBNJKRELNBJNKRENBJKLNTJBERBLBVCB";

    public String generateToken(String username) {
        long jwtExpirationMs = 86400000;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("Error related to the token");
        }

        return false;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
