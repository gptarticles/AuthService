package me.zedaster.authservice.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.zedaster.authservice.annotation.Nickname;

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
    @Nickname
    private String newUsername;

    /**
     * Password of the user to confirm the change
     */
    private String password;
}
