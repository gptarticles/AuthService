package me.zedaster.authservice.dto.profile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.zedaster.authservice.annotation.ExactPassword;
import me.zedaster.authservice.annotation.ShallowPassword;

/**
 * DTO for changing the password of the user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {
    @NotNull(message = "Old password can't be null!")
    @ShallowPassword
    private String oldPassword;

    @NotNull(message = "New password can't be null!")
    @ExactPassword
    private String newPassword;
}
