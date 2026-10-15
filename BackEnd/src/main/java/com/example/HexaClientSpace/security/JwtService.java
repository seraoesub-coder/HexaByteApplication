package com.example.HexaClientSpace.security;



import com.example.HexaClientSpace.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Generation et validation des JWT (jjwt 0.12.x).
 * Le token porte un claim custom "userId" pour eviter une requete SQL
 * a chaque appel juste pour retrouver l'identifiant de l'utilisateur.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secretBase64,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        // La cle doit faire au moins 256 bits pour HS256.
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        if (user.getClient() != null) {
            claims.put("numeroClient", user.getClient().getNumeroClient());
        }

        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /** Identifiant utilisateur, lu depuis le claim custom. */
    public Long extractUserId(String token) {
        Object raw = parseClaims(token).get("userId");
        return raw == null ? null : Long.valueOf(raw.toString());
    }

    public boolean isValid(String token) {
        try {
            return parseClaims(token).getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
