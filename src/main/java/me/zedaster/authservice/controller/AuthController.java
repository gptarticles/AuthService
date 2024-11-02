package me.zedaster.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.zedaster.authservice.dto.SuccessDto;
import me.zedaster.authservice.dto.auth.*;
import me.zedaster.authservice.exception.AuthException;
import me.zedaster.authservice.exception.JwtException;
import me.zedaster.authservice.model.User;
import me.zedaster.authservice.service.JwtService;
import me.zedaster.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Controller for handling authentication requests.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    /**
     * Service for working with users.
     */
    private final UserService userService;

    /**
     * Service for working with JWT tokens.
     */
    private final JwtService jwtService;

    /**
     * Register a new user.
     * @param registerDto DTO of the user to register.
     */
    @PostMapping("/register")
    public JwtPairDto register(@Valid @RequestBody NewUserDto registerDto) throws AuthException {
        User user = userService.createUser(registerDto);
        return jwtService.generateTokens(user.getId(), user.getUsername());
    }

    /**
     * Log in a user.
     * @param userCredentialsDto Credentials of the user.
     * @return DTO of JWT access token and refresh token.
     */
    @PostMapping("/login")
    public JwtPairDto login(@Valid @RequestBody UserCredentialsDto userCredentialsDto) throws AuthException {
        Optional<User> user = userService.getUser(userCredentialsDto);
        if (user.isEmpty()) {
            throw AuthException.newInvalidCredentialsException();
        }
        return jwtService.generateTokens(user.get().getId(), user.get().getUsername());
    }

    /**
     * Verify the JWT access token.
     * @param accessTokenDto DTO of JWT access token.
     * @return DTO with the result of the verification.
     */
    @PostMapping("/verifyToken")
    public ResponseEntity<SuccessDto> verifyToken(@RequestBody AccessTokenDto accessTokenDto) throws JwtException {
        jwtService.validateAccessToken(accessTokenDto.getAccessToken());
        return ResponseEntity.ok(new SuccessDto(true));
    }

    /**
     * Refresh the JWT access token.
     * @param refreshTokenDto DTO of JWT Refresh token.
     * @return DTO of the new JWT access token and refresh token.
     */
    @PostMapping("/refreshToken")
    public JwtPairDto refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) throws JwtException {
        return jwtService.refreshToken(refreshTokenDto.getRefreshToken());
    }
}
