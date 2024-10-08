package me.zedaster.authservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO of JWT access token and refresh token.
 */
@Data
@AllArgsConstructor
public class JwtPairDto {
    /**
     * JWT access token.
     */
    private final String accessToken;

    /**
     * Token for refreshing the pair of access and refresh tokens.
     */
    private final String refreshToken;
}
