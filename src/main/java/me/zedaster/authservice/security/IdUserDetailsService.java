package me.zedaster.authservice.security;

import lombok.AllArgsConstructor;
import me.zedaster.authservice.exception.UserByIdNotFoundException;
import me.zedaster.authservice.model.User;
import me.zedaster.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading user details (for Spring Security) by ID.
 */
@Service
@AllArgsConstructor
public class IdUserDetailsService implements UserDetailsService {

    /**
     * Repository for working with users.
     */
    private final UserRepository userRepository;

    /**
     * Loads user details by ID.
     * @param userId User ID.
     * @return User details.
     */
    public IdUserDetails loadUserById(long userId) throws UserByIdNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserByIdNotFoundException("User with ID %d not found".formatted(userId)));
        return new IdUserDetails(user.getId(), user.getUsername(), user.getPassword());
    }

    /**
     * Loads user details by username.
     * @param username Username.
     * @return User details.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username %s not found".formatted(username)));

        return new IdUserDetails(user.getId(), user.getUsername(), user.getPassword());
    }

    /**
     * Loads user details by username or email.
     * @param usernameOrEmail Username or email.
     * @return User details.
     */
    public IdUserDetails loadUserByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User with username or email %s not found".formatted(usernameOrEmail)));

        return new IdUserDetails(user.getId(), user.getUsername(), user.getPassword());
    }
}
