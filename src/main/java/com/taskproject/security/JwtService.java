package com.taskproject.security;

import com.taskproject.entity.User;
import com.taskproject.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service responsible for generating and validating JWT tokens.
 * <p>
 * The {@code jwt.secret} property MUST be a Base64-encoded representation of a
 * random byte array that is at least 32 bytes long (256 bits) so that JJWT's
 * {@code Keys.hmacShaKeyFor()} can build a valid HS256 signing key.
 * </p>
 * <p>
 * Generate a suitable secret with PowerShell:<br>
 * {@code [Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))}
 * </p>
 */
@Service
public class JwtService {

    private final Key key;
    private final long jwtExpirationMs;

    public JwtService(@Value("${jwt.secret}") String base64Secret,
                      @Value("${jwt.expiration-ms}") long expirationMs) {
        // Decode the Base64 string to raw bytes, then build the HMAC key.
        // This guarantees at least 256 bits when the secret was generated correctly.
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = expirationMs;
    }

    /**
     * Generate a JWT token for the given {@link User}.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the username (email) from a token.
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extract the role claim from a token.
     */
    public Role extractRole(String token) {
        String roleStr = (String) getClaims(token).get("role");
        return Role.valueOf(roleStr);
    }

    /**
     * Validate the token – checks signature and expiration.
     */
    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(Date.from(Instant.now()));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
