package me.zedaster.authservice.exception;

/**
 * Exception thrown when an error occurs with the JWT.
 */
public class JwtException extends Exception {
    public JwtException(String message) {
        super(message);
    }
}
