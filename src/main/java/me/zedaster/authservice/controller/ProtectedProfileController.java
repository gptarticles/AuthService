package me.zedaster.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.zedaster.authservice.dto.TokenPayload;
import me.zedaster.authservice.dto.auth.JwtPairDto;
import me.zedaster.authservice.dto.profile.ChangePasswordDto;
import me.zedaster.authservice.dto.profile.ChangeUsernameDto;
import me.zedaster.authservice.exception.AuthException;
import me.zedaster.authservice.exception.ProfileException;
import me.zedaster.authservice.exception.UserIdException;
import me.zedaster.authservice.model.Role;
import me.zedaster.authservice.model.User;
import me.zedaster.authservice.service.JwtService;
import me.zedaster.authservice.service.UserService;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("")
    public User getUserProfile(@RequestParam("tokenPayload.sub") String sub) {
        long userId = Long.parseLong(sub);
        return userService.getUser(userId).orElseThrow(() -> UserIdException.newNotFoundException(userId));
    }

    @PostMapping("/changeUsername")
    @Transactional
    public JwtPairDto changeUsername(@Valid @RequestBody ChangeUsernameDto changeUsernameDto,
                                     @RequestParam("tokenPayload.sub") String sub,
                                     @RequestParam("tokenPayload.username") String username,
                                     @RequestParam("tokenPayload.role") Role role)
            throws AuthException, ProfileException {
        TokenPayload tokenPayload = new TokenPayload(sub, username, role);
        if (!userService.isPasswordCorrect(tokenPayload.getUserId(), changeUsernameDto.getPassword())) {
            throw new AuthException("The password is incorrect!");
        }

        userService.changeUsername(tokenPayload.getUserId(), changeUsernameDto.getNewUsername());
        TokenPayload newTokenPayload = tokenPayload.copy();
        newTokenPayload.setUsername(changeUsernameDto.getNewUsername());
        return jwtService.generateTokens(newTokenPayload);
    }

    @PostMapping("/changePassword")
    @Transactional
    public JwtPairDto changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto,
                                     @RequestParam("tokenPayload.sub") String sub,
                                     @RequestParam("tokenPayload.username") String username,
                                     @RequestParam("tokenPayload.role") Role role) throws AuthException {
        TokenPayload tokenPayload = new TokenPayload(sub, username, role);
        if (!userService.isPasswordCorrect(tokenPayload.getUserId(), changePasswordDto.getOldPassword())) {
            throw new AuthException("The password is incorrect!");
        }

        userService.changePassword(tokenPayload.getUserId(), changePasswordDto.getNewPassword());
        return jwtService.generateTokens(tokenPayload);
    }
}
