package me.zedaster.authservice.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.zedaster.authservice.annotation.ShallowPassword;

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
    // 254 is max size of an email
    @NotNull(message = "Username or email must be not null!")
    @Pattern(regexp = "^[a-zA-Z0-9._+-@]{3,254}$",
            message = "Username or email is incorrect!")
    private String usernameOrEmail;

    /**
     * Password of the user.
     */
    @NotNull(message = "Password must be not null!")
    @ShallowPassword
    private String password;
}
