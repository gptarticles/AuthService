package me.zedaster.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.zedaster.authservice.dto.TokenPayload;
import me.zedaster.authservice.dto.auth.JwtPairDto;
import me.zedaster.authservice.exception.JwtException;
import me.zedaster.authservice.model.Role;
import me.zedaster.authservice.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service for operations with JWT.
 */
@Service
public class JwtService {
    /**
     * Lifetime of the JWT access token in milliseconds. (5 minutes)
     */
    private static final long ACCESS_TOKEN_LIFETIME = 5 * 60 * 1000L;

    /**
     * Lifetime of the JWT refresh token in milliseconds. (30 days)
     */
    private static final long REFRESH_TOKEN_LIFETIME = 30 * 24 * 60 * 60 * 1000L;

    /**
     * Secret key for signing the JWT access token.
     */
    private final SecretKey accessSecretKey;

    /**
     * Secret key for signing the JWT refresh token.
     */
    private final SecretKey refreshSecretKey;

    public JwtService(@Value("${jwt.secret.access}") String accessTokenSecret,
                      @Value("${jwt.secret.refresh}") String refreshTokenSecret) {
        this.accessSecretKey = getSecretKey(accessTokenSecret);
        this.refreshSecretKey = getSecretKey(refreshTokenSecret);
    }

    /**
     * Validate the JWT access token.
     * @param accessToken JWT access token.
     * @throws JwtException If the token is invalid.
     */
    public void validateAccessToken(String accessToken) throws JwtException {
        try {
            Jwts
                    .parser()
                    .verifyWith(accessSecretKey)
                    .build()
                    .parseSignedClaims(accessToken);
        } catch (io.jsonwebtoken.JwtException e) {
            throw new JwtException("The access token is invalid!");
        }

    }

    /**
     * Generate a pair of JWT access and refresh tokens.
     * @param user User for whom the tokens are generated.
     * @return Pair of JWT access and refresh tokens.
     */
    public JwtPairDto generateTokens(User user) {
        TokenPayload payload = new TokenPayload(user.getId(), user.getUsername(), user.getRole());
        return generateTokens(payload);
    }

    /**
     * Generate a pair of JWT access and refresh tokens.
     * @param payload Payload of the tokens.
     * @return Pair of JWT access and refresh tokens.
     */
    public JwtPairDto generateTokens(TokenPayload payload) {
        String accessToken = Jwts.builder()
                .claim("sub", String.valueOf(payload.getUserId()))
                .claim("username", payload.getUsername())
                .claim("role", payload.getRole().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_LIFETIME))
                .signWith(accessSecretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .claim("sub", String.valueOf(payload.getUserId()))
                .claim("username", payload.getUsername())
                .claim("role", payload.getRole().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_LIFETIME))
                .signWith(refreshSecretKey)
                .compact();

        return new JwtPairDto(accessToken, refreshToken);
    }

    /**
     * Refresh the pair of JWT access and refresh tokens.
     * @param refreshToken Refresh token.
     * @return New pair of JWT access and refresh tokens.
     * @throws JwtException If the refresh token is invalid.
     */
    public JwtPairDto refreshToken(String refreshToken) throws JwtException {
        TokenPayload tokenPayload;

        try {
            Claims claims = Jwts
                    .parser()
                    .verifyWith(refreshSecretKey)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
            long sub = Long.parseLong(claims.get("sub", String.class));
            String username = claims.get("username", String.class);
            Role role = Role.valueOf(claims.get("role", String.class));
            tokenPayload = new TokenPayload(sub, username, role);
        } catch (io.jsonwebtoken.JwtException e) {
            throw new JwtException("The refresh token is invalid!");
        }

        return generateTokens(tokenPayload);
    }

    /**
     * Transform a secret string to a secret key.
     * @param secret Secret string.
     * @return Secret key.
     */
    private SecretKey getSecretKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
