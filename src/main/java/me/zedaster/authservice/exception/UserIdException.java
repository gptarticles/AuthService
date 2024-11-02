package me.zedaster.authservice.exception;

/**
 * Runtime exception related to userId
 */
public class UserIdException extends IllegalArgumentException {
    private UserIdException(String message) {
        super(message);
    }

    public static UserIdException newIncorrectException() {
        return new UserIdException("The user ID is incorrect!");
    }

    public static UserIdException newNotFoundException(long userId) {
        return new UserIdException("User with ID %d not found!".formatted(userId));
    }
}
