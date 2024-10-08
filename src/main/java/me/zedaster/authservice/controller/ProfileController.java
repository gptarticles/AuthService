package me.zedaster.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.zedaster.authservice.dto.auth.JwtPairDto;
import me.zedaster.authservice.dto.profile.ChangeUsernameDto;
import me.zedaster.authservice.exception.AuthException;
import me.zedaster.authservice.exception.ProfileException;
import me.zedaster.authservice.security.IdUserDetails;
import me.zedaster.authservice.security.auth.UsernameOrEmailAuthentication;
import me.zedaster.authservice.service.JwtService;
import me.zedaster.authservice.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing user profile data.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    /**
     * Service for working with users.
     */
    private final UserService userService;

    /**
     * Utils for working with JWT token.
     */
    private final JwtService jwtService;

    /**
     * Password encoder.
     */
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/changeUsername")
    public JwtPairDto changeUsername(@Valid @RequestBody ChangeUsernameDto changeUsernameDto)
            throws AuthException, ProfileException {
        UsernameOrEmailAuthentication authentication = (UsernameOrEmailAuthentication) SecurityContextHolder
                .getContext().getAuthentication();
        IdUserDetails userDetails = (IdUserDetails) authentication.getDetails();
        long userId = userDetails.getId();
        String hashedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(changeUsernameDto.getPassword(), hashedPassword)) {
            throw new AuthException("The password is incorrect!");
        }

        userService.changeUsername(userId, changeUsernameDto.getNewUsername());
        return jwtService.generateTokens(userId, changeUsernameDto.getNewUsername());
    }
}
