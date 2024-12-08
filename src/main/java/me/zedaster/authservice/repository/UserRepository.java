package me.zedaster.authservice.repository;

import me.zedaster.authservice.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Finds all usernames by user IDs.
     * @param ids User IDs.
     * @return List of usernames.
     */
    @Query("SELECT u.username FROM User u WHERE u.id IN :ids")
    List<String> findAllUsernamesById(Iterable<Long> ids);
}
