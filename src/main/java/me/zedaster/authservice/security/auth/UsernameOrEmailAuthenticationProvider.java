package me.zedaster.authservice.security.auth;

import lombok.AllArgsConstructor;
import me.zedaster.authservice.security.IdUserDetails;
import me.zedaster.authservice.security.IdUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Authentication provider that supports authentication with username or email and password.
 */
@Component
@AllArgsConstructor
public class UsernameOrEmailAuthenticationProvider implements AuthenticationProvider {
    /**
     * Service for working with user details.
     */
    private final IdUserDetailsService idUserDetailsService;

    /**
     * Password encoder.
     */
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String usernameOrEmail = authentication.getName();
        String password = (String) authentication.getCredentials();

        IdUserDetails userDetails;
        try {
            userDetails = idUserDetailsService.loadUserByUsernameOrEmail(usernameOrEmail);
        } catch (UsernameNotFoundException e) {
            return new UsernameOrEmailAuthentication(usernameOrEmail, password);
        }


        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernameOrEmailAuthentication(usernameOrEmail, password);
        }

        return UsernameOrEmailAuthentication.createAuthenticated(userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernameOrEmailAuthentication.class.isAssignableFrom(authentication);
    }
}
