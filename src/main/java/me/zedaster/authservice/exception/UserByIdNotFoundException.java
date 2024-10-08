package me.zedaster.authservice.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when a user with a given ID is not found. Created for a Spring Security details service.
 */
public class UserByIdNotFoundException extends AuthenticationException {
    public UserByIdNotFoundException(String msg) {
        super(msg);
    }
}
