package me.zedaster.authservice.exception;

/**
 * Exception for authentication errors.
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }

    public static AuthException newInvalidCredentialsException() {
        return new AuthException("The username or password are incorrect!");
    }
}
