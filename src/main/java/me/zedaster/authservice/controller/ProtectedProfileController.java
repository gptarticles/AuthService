package me.zedaster.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.zedaster.authservice.dto.auth.JwtPairDto;
import me.zedaster.authservice.dto.profile.ChangePasswordDto;
import me.zedaster.authservice.dto.profile.ChangeUsernameDto;
import me.zedaster.authservice.exception.AuthException;
import me.zedaster.authservice.exception.ProfileException;
import me.zedaster.authservice.service.JwtService;
import me.zedaster.authservice.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user profile data.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/protected/profile")
public class ProtectedProfileController {

    /**
     * Service for working with users.
     */
    private final UserService userService;

    /**
     * Utils for working with JWT token.
     */
    private final JwtService jwtService;

    @PostMapping("/changeUsername")
    public JwtPairDto changeUsername(@Valid @RequestBody ChangeUsernameDto changeUsernameDto,
                                     @RequestParam("tokenPayload.sub") Long userId)
            throws AuthException, ProfileException {
        if (!userService.isPasswordCorrect(userId, changeUsernameDto.getPassword())) {
            throw new AuthException("The password is incorrect!");
        }

        userService.changeUsername(userId, changeUsernameDto.getNewUsername());
        return jwtService.generateTokens(userId, changeUsernameDto.getNewUsername());
    }

    @PostMapping("/changePassword")
    public JwtPairDto changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto,
                               @RequestParam("tokenPayload.sub") Long userId) throws AuthException {
        if (!userService.isPasswordCorrect(userId, changePasswordDto.getOldPassword())) {
            throw new AuthException("The password is incorrect!");
        }

        userService.changePassword(userId, changePasswordDto.getNewPassword());
        String username = userService.getUsername(userId);
        return jwtService.generateTokens(userId, username);
    }
}
