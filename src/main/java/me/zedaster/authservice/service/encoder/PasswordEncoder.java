package me.zedaster.authservice.service.encoder;

/**
 * Service to hash passwords
 */
public interface PasswordEncoder {

    /**
     * Encodes password
     * @param rawPassword The raw password
     * @return Encoded password hash
     */
    String encode(CharSequence rawPassword);

    /**
     * Compares raw password and encoded password
     * @param rawPassword The raw password
     * @param encodedPassword The encoded password
     * @return true if the password are the same
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
