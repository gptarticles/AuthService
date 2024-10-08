package me.zedaster.authservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO of the JWT refresh token.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {
    /**
     * JWT refresh token.
     */
    private String refreshToken;
}
