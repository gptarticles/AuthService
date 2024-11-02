package me.zedaster.authservice.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.zedaster.authservice.annotation.ExactPassword;
import me.zedaster.authservice.annotation.Nickname;

/**
 * DTO for registering a user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    /**
     * Username of the user
     * <br/><br/>
     * Requirements:
     * <ul>
     *     <li>At least 3 characters</li>
     *     <li>No more than 32 characters</li>
     *     <li>May contain latin letters, digits, periods and underscores only</li>
     *     <li>Must start with a letter</li>
     *     <li>Must be not null</li>
     *  </ul>
     */
    @NotNull(message = "Username must be not null!")
    @Nickname
    private String username;

    /**
     * Password of the user
     * <br/><br/>
     * Must meet <a href="https://support.kaspersky.com/KPC/1.0/en-US/183862.htm">Kaspersky password requirements</a>
     * (but without cyrillic letters):
     * <ul>
     *     <li>At least 8 characters</li>
     *     <li>No more than 128 characters</li>
     *     <li>At least one uppercase and one lowercase letter</li>
     *     <li>Latin letters only</li>
     *     <li>At least one numeral</li>
     *     <li>Arabic numerals only</li>
     *     <li>No spaces</li>
     *     <li>Other characters that are also valid: ~ ! ? @ # $ % ^ & * _ - + ( ) [ ] { } > < / \ | " ' . , : ;</li>
     * </ul>
     */
    @NotNull(message = "Password must be not null!")
    @ExactPassword
    private String password;

    /**
     * Email of the user
     * <br/><br/>
     * Must contain a correct email address
     */
    @NotEmpty(message = "The email must not be empty!")
    @Email(message = "The email is incorrect!")
    private String email;
}
