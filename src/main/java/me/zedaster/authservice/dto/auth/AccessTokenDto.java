package me.zedaster.authservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO of the JWT access token.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenDto {
    /**
     * JWT access token.
     */
    private String accessToken;
}
