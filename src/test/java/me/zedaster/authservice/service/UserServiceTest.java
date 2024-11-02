package me.zedaster.authservice.service;

import me.zedaster.authservice.dto.auth.UserCredentialsDto;
import me.zedaster.authservice.exception.ProfileException;
import me.zedaster.authservice.model.User;
import me.zedaster.authservice.repository.UserRepository;
import me.zedaster.authservice.service.encoder.PasswordEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link UserService}
 */
@SpringBootTest(classes = {UserService.class})
public class UserServiceTest {
    // Now there are only tests for UserService#getUser and UserService#changeUsername

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    /**
     * Test for getting user with right credentials
     */
    @Test
    public void getExistingUserWithRightPassword() {
        User fakeUser = mock(User.class);
        when(fakeUser.getId()).thenReturn(1L);
        when(fakeUser.getUsername()).thenReturn("user");
        when(fakeUser.getPassword()).thenReturn("encryptedPass");
        when(userRepository.findByUsernameOrEmail("user", "user")).thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.matches("rawPass", "encryptedPass")).thenReturn(true);

        Optional<User> user = userService.getUser(new UserCredentialsDto("user", "rawPass"));
        Assertions.assertTrue(user.isPresent());
        Assertions.assertSame(fakeUser, user.get());
    }

    /**
     * Test for getting user with wrong password
     */
    @Test
    public void getExistentUserWithWrongPassword() {
        User fakeUser = mock(User.class);
        when(fakeUser.getId()).thenReturn(1L);
        when(fakeUser.getUsername()).thenReturn("user");
        when(fakeUser.getPassword()).thenReturn("encryptedPass");
        when(userRepository.findByUsernameOrEmail("user", "user")).thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.matches("wrongPass", "encryptedPass")).thenReturn(false);

        Optional<User> user = userService.getUser(new UserCredentialsDto("user", "wrongPass"));

        verify(userRepository, times(1)).findByUsernameOrEmail(anyString(), anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        Assertions.assertTrue(user.isEmpty());
    }

    /**
     * Test for getting user that doesn't exist
     */
    @Test
    public void getNonExistentUser() {
        when(userRepository.findByUsernameOrEmail("user", "user")).thenReturn(Optional.empty());

        Optional<User> user = userService.getUser(new UserCredentialsDto("user", "rawPass"));

        verify(userRepository, times(1)).findByUsernameOrEmail(anyString(), anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        Assertions.assertTrue(user.isEmpty());
    }

    /**
     * Test for successful changing of the username
     */
    @Test
    public void changeUsername() throws Exception {
        when(userRepository.existsByUsername("newname")).thenReturn(false);
        User fakeUser = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        userService.changeUsername(1L, "newname");

        verify(userRepository, times(1)).existsByUsername("newname");
        verify(userRepository, times(1)).findById(1L);
        verify(fakeUser, times(1)).setUsername("newname");
        verify(userRepository, times(1)).save(same(fakeUser));
    }

    /**
     * Test for changing the username if the new username is already taken.
     */
    @Test
    public void changeUsernameTaken() throws Exception {
        when(userRepository.existsByUsername("newname")).thenReturn(true);

        ProfileException ex = assertThrows(ProfileException.class,
                () -> userService.changeUsername(1L, "newname"));
        assertEquals("The username is already taken!", ex.getMessage());
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any());
    }

    /**
     * Test for changing the username with incorrect user ID.
     */
    @Test
    public void changeUsernameIncorrectUserId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.changeUsername(0L, "newname"));
        assertEquals("The user ID is incorrect!", ex.getMessage());
        verify(userRepository, never()).existsByUsername(anyString());
    }

    /**
     * Test for changing the username with non-existent user ID.
     */
    @Test
    public void changeUsernameNonExistentUserId() {
        when(userRepository.existsByUsername("newname")).thenReturn(false);
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.changeUsername(100L, "newname"));
        assertEquals("User with ID 100 not found!", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

}
