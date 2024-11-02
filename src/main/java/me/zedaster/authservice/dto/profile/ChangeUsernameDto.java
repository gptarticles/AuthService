package me.zedaster.authservice.dto.profile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.zedaster.authservice.annotation.Nickname;
import me.zedaster.authservice.annotation.ShallowPassword;

/**
 * DTO for changing the username of the user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUsernameDto {
    /**
     * New username of the user
     */
    @NotNull(message = "Username can't be null!")
    @Nickname
    private String newUsername;

    /**
     * Password of the user to confirm the change
     */
    @NotNull(message = "Password can't be null!")
    @ShallowPassword
    private String password;
}
