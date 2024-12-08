package me.zedaster.authservice.exception;

/**
 * Runtime exception related to userId
 */
public class UserIdException extends IllegalArgumentException {
    private UserIdException(String message) {
        super(message);
    }

    public static UserIdException newIncorrectException(long userId) {
        return new UserIdException("User ID %d is incorrect!".formatted(userId));
    }

    public static UserIdException newNotFoundException(long userId) {
        return new UserIdException("User with ID %d not found!".formatted(userId));
    }

    public static UserIdException newManyNotFoundException() {
        return new UserIdException("Some users not found!");
    }
}
