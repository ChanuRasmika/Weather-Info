package org.example.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.backend.entity.Token;
import org.example.backend.entity.User;
import org.example.backend.repository.TokenRepository;
import org.example.backend.util.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final TokenRepository tokenRepository;

    public JwtService(
            @Value("${security.jwt.secret-key}") String base64SecretKey,
            @Value("${security.jwt.expiration-time}") long accessTokenExpiration,
            @Value("${security.jwt.refresh-expiration-time}") long refreshTokenExpiration,
            TokenRepository tokenRepository
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64SecretKey));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.tokenRepository = tokenRepository;
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        return generateToken(userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(CustomUserDetails userDetails) {
        return generateToken(userDetails, refreshTokenExpiration);
    }

    private String generateToken(CustomUserDetails userDetails, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        User user = userDetails.getUser();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    @Transactional
    public void saveToken(String token, User user, Token.TokenType tokenType) {
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setTokenType(tokenType);
        tokenEntity.setExpired(false);
        tokenEntity.setRevoked(false);
        tokenEntity.setUser(user);
        
        long expiration = tokenType == Token.TokenType.ACCESS_TOKEN ? accessTokenExpiration : refreshTokenExpiration;
        tokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(expiration / 1000));
        
        tokenRepository.save(tokenEntity);
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        var tokenEntity = tokenRepository.findByToken(token);
        return username.equals(userDetails.getUsername()) 
                && !isTokenExpired(token)
                && tokenEntity.map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public long getExpirationTime() {
        return accessTokenExpiration;
    }
}