package me.zedaster.authservice.service;

import lombok.AllArgsConstructor;
import me.zedaster.authservice.dto.auth.NewUserDto;
import me.zedaster.authservice.dto.auth.UserCredentialsDto;
import me.zedaster.authservice.exception.AuthException;
import me.zedaster.authservice.exception.ProfileException;
import me.zedaster.authservice.exception.UserIdException;
import me.zedaster.authservice.model.User;
import me.zedaster.authservice.repository.UserRepository;
import me.zedaster.authservice.service.encoder.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for working with users
 */
@Service
@AllArgsConstructor
public class UserService {

    /**
     * Repository for storing users.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user.
     * @param newUserDto DTO with information of the user to register.
     * @throws AuthException If the username or email is already taken.
     * @return ID of the registered user.
     */
    @Transactional
    public User createUser(NewUserDto newUserDto) throws AuthException {
        String username = newUserDto.getUsername();
        String email = newUserDto.getEmail();
        String password = newUserDto.getPassword();

        if (userRepository.existsByUsername(username)) {
            throw new AuthException("User with the same username already exists!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new AuthException("User with the same email already exists!");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
        return userRepository.save(user);
    }

    /**
     * Finds a user with specified credentials
     * @param credentials Credentials of the user
     * @return User or empty optional if user with these credentials doesn't exist
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Optional<User> getUser(UserCredentialsDto credentials) {
        String usernameOrEmail = credentials.getUsernameOrEmail();
        String rawPassword = credentials.getPassword();
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        String encryptedPassword = optionalUser.get().getPassword();
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            return Optional.empty();
        }

        return optionalUser;
    }

    /**
     * Returns username of user by its ID
     * @param userId ID of the user
     * @return username of the user
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public String getUsername(long userId) {
        if (userId <= 0) {
            throw UserIdException.newIncorrectException(userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserIdException.newNotFoundException(userId));
        return user.getUsername();
    }

    /**
     * Returns usernames of users by their IDs
     * @param userIds IDs of the users
     * @return List of usernames
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getUsernames(List<Long> userIds) {
        for (long userId : userIds) {
            if (userId <= 0) {
                throw UserIdException.newIncorrectException(userId);
            }
        }

        List<String> result = userRepository.findAllUsernamesById(userIds);
        if (result.size() != userIds.size()) {
            throw UserIdException.newManyNotFoundException();
        }
        return result;
    }

    /**
     * Checks if the password belongs to the user with specified id
     * @param userId ID of the user
     * @param rawPassword Password to check
     * @return True if it is the right password of the user
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isPasswordCorrect(long userId, String rawPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }

        String encryptedPassword = optionalUser.get().getPassword();
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }

    /**
     * Change the username of the user.
     * @param userId ID of the user.
     * @param newUsername New valid username.
     * @throws ProfileException If the username is already taken.
     */
    @Transactional
    public void changeUsername(long userId, String newUsername) throws ProfileException {
        if (userId <= 0) {
            throw UserIdException.newIncorrectException(userId);
        }

        boolean isUsernameTaken = userRepository.existsByUsername(newUsername);
        if (isUsernameTaken) {
            throw new ProfileException("The username is already taken!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserIdException.newNotFoundException(userId));
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    /**
     * Changes the password of the user
     * @param userId ID of the user
     * @param newPassword New valid password
     */
    @Transactional
    public void changePassword(long userId, String newPassword) {
        if (userId <= 0) {
            throw UserIdException.newIncorrectException(userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserIdException.newNotFoundException(userId));
        String newHash = passwordEncoder.encode(newPassword);
        user.setPassword(newHash);
        userRepository.save(user);
    }
}
