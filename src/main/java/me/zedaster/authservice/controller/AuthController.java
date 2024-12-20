package me.zedaster.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.zedaster.authservice.dto.SuccessDto;
import me.zedaster.authservice.dto.auth.JwtPairDto;
import me.zedaster.authservice.dto.auth.NewUserDto;
import me.zedaster.authservice.dto.auth.UserCredentialsDto;
import me.zedaster.authservice.exception.AuthException;
import me.zedaster.authservice.exception.JwtException;
import me.zedaster.authservice.model.User;
import me.zedaster.authservice.service.JwtService;
import me.zedaster.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return jwtService.generateTokens(user);
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
        return jwtService.generateTokens(user.get());
    }

    /**
     * Verify the JWT access token.
     * @param accessToken Access token
     * @return DTO with the result of the verification.
     */
    @GetMapping("/verifyToken")
    public ResponseEntity<SuccessDto> verifyToken(@RequestParam String accessToken) throws JwtException {
        jwtService.validateAccessToken(accessToken);
        return ResponseEntity.ok(new SuccessDto(true));
    }

    /**
     * Refresh a pair of JWT tokens
     * @param refreshToken Refresh token.
     * @return DTO of the new JWT access token and refresh token.
     */
    @GetMapping("/refreshToken")
    public JwtPairDto refreshToken(@RequestParam String refreshToken) throws JwtException {
        return jwtService.refreshToken(refreshToken);
    }
}
