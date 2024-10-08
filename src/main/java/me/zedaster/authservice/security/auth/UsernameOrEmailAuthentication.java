package me.zedaster.authservice.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Authentication with username or email and password.
 */
public class UsernameOrEmailAuthentication implements Authentication {
    /**
     * Username or email of the user.
     */
    private final String usernameOrEmail;

    /**
     * Password of the user.
     */
    private final String password;

    /**
     * User details.
     */
    private final UserDetails userDetails;

    /**
     * Status of the authentication.
     */
    private boolean authenticated;

    /**
     * Creates unauthenticated object.
     * @param usernameOrEmail Username or email of the user.
     * @param password Password of the user.
     */
    public UsernameOrEmailAuthentication(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
        this.authenticated = false;
        this.userDetails = null;
    }

    /**
     * Creates authenticated object with user details.
     * @param userDetails User details.
     */
    private UsernameOrEmailAuthentication(UserDetails userDetails) {
        this.usernameOrEmail = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.authenticated = true;
        this.userDetails = userDetails;
    }

    /**
     * Creates authenticated object with user details.
     * @param userDetails User details.
     * @return An instance of this class with authentication status set to true and user details.
     */
    public static UsernameOrEmailAuthentication createAuthenticated(UserDetails userDetails) {
        return new UsernameOrEmailAuthentication(userDetails);
    }

    @Override
    public Object getPrincipal() {
        return usernameOrEmail;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getDetails() {
        return this.userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return usernameOrEmail;
    }
}
