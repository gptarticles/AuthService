package me.zedaster.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.zedaster.authservice.dto.auth.JwtPairDto;
import me.zedaster.authservice.exception.JwtException;
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
        this.accessSecretKey = getSecretKey(accessTokenSecret);;
        this.refreshSecretKey = getSecretKey(refreshTokenSecret);
    }

    /**
     * Extract the user ID from the JWT access token.
     * @param accessToken JWT access token.
     * @throws JwtException If the token is invalid.
     * @return User ID.
     */
    public long extractUserId(String accessToken) throws JwtException {
        try {
            return Jwts
                    .parser()
                    .verifyWith(accessSecretKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload()
                    .get("userId", Long.class);
        } catch (io.jsonwebtoken.JwtException e) {
            throw new JwtException("Invalid access token");
        }
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
     * @param userId User ID.
     * @param username Username.
     * @return Pair of JWT access and refresh tokens.
     */
    public JwtPairDto generateTokens(long userId, String username) {
        String accessToken = Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_LIFETIME))
                .signWith(accessSecretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
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
        long userId;
        String username;

        try {
            Claims claims = Jwts
                    .parser()
                    .verifyWith(refreshSecretKey)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
            userId = claims.get("userId", Long.class);
            username = claims.get("username", String.class);
        } catch (io.jsonwebtoken.JwtException e) {
            throw new JwtException("The refresh token is invalid!");
        }

        return generateTokens(userId, username);
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
