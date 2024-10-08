package me.zedaster.authservice.exception;

/**
 * Exception for errors related to managing user profile data.
 */
public class ProfileException extends Exception {
    public ProfileException(String message) {
        super(message);
    }
}
