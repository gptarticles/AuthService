package me.zedaster.authservice.repository;

import me.zedaster.authservice.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for storing users in the database.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Find a user by username or email.
     * @param username Username.
     * @param email Email.
     * @return User with the given username or email.
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Find a user by username.
     * @param username Username.
     * @return User with the given username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if user with the given username exists.
     * @param username Username to check.
     * @return True if user with the given username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if user with the given email exists.
     * @param email Email to check.
     * @return True if user with the given email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
