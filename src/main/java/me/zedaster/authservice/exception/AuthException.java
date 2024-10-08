package me.zedaster.authservice.exception;

/**
 * Exception for authentication errors.
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
