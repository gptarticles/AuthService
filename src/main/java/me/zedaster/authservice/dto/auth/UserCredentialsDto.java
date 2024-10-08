package me.zedaster.authservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for logging in a user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDto {
    /**
     * Username or email of the user.
     */
    private String usernameOrEmail;

    /**
     * Password of the user.
     */
    private String password;
}
