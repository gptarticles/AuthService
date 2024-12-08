package me.zedaster.authservice.service;

import me.zedaster.authservice.dto.auth.UserCredentialsDto;
import me.zedaster.authservice.exception.ProfileException;
import me.zedaster.authservice.exception.UserIdException;
import me.zedaster.authservice.model.User;
import me.zedaster.authservice.repository.UserRepository;
import me.zedaster.authservice.service.encoder.PasswordEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link UserService}
 */
@SpringBootTest(classes = {UserService.class})
public class UserServiceTest {
    // Now there are only tests for UserService#getUser, UserService#getUsername, UserService#changeUsername

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
     * Test for getting username by right userId
     */
    @Test
    public void getUsernameByRightId() {
        User fakeUser = mock(User.class);
        when(fakeUser.getUsername()).thenReturn("barbra.streisand");
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        String username = userService.getUsername(1L);
        assertEquals("barbra.streisand", username);
    }

    /**
     * Test for getting username by incorrect userId
     */
    @Test
    public void getUsernameByIncorrectId() {
        UserIdException ex = assertThrows(UserIdException.class,
                () -> userService.getUsername(0L));
        assertEquals("User ID 0 is incorrect!", ex.getMessage());
        verify(userRepository, never()).findById(anyLong());
    }

    /**
     * Test for getting username by userId that doesn't exist
     */
    @Test
    public void getUsernameByNonExistentId() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        UserIdException ex = assertThrows(UserIdException.class, () ->
                userService.getUsername(100L));
        assertEquals("User with ID 100 not found!", ex.getMessage());
    }

    /**
     * Test for getting usernames by right userIds
     */
    @Test
    public void getUsernamesByRightIds() {
        when(userRepository.findAllUsernamesById(List.of(1L, 2L))).thenReturn(List.of("barbra.streisand", "john.doe"));

        List<String> usernames = userService.getUsernames(List.of(1L, 2L));
        assertEquals(List.of("barbra.streisand", "john.doe"), usernames);
    }

    /**
     * Test for getting usernames by userIds with incorrect one
     */
    @Test
    public void getUsernamesByIdsWithIncorrectOne() {
        UserIdException ex = assertThrows(UserIdException.class,
                () -> userService.getUsernames(List.of(2L, 0L)));
        assertEquals("User ID 0 is incorrect!", ex.getMessage());
        verify(userRepository, never()).findAllUsernamesById(any());
    }

    /**
     * Test for getting usernames by userIds with non-existent one
     */
    @Test
    public void getUsernamesByIdsWithNonExistentOne() {
        when(userRepository.findAllUsernamesById(List.of(100L, 200L))).thenReturn(List.of("barbra.streisand"));

        UserIdException ex = assertThrows(UserIdException.class,
                () -> userService.getUsernames(List.of(100L, 200L)));
        assertEquals("Some users not found!", ex.getMessage());
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
        verify(fakeUser, times(1)).setUsername("newname");
        verify(userRepository, times(1)).save(same(fakeUser));
    }

    /**
     * Test for changing the username if the new username is already taken.
     */
    @Test
    public void changeUsernameTaken()  {
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
        UserIdException ex = assertThrows(UserIdException.class,
                () -> userService.changeUsername(0L, "newname"));
        assertEquals("User ID 0 is incorrect!", ex.getMessage());
        verify(userRepository, never()).existsByUsername(anyString());
    }

    /**
     * Test for changing the username with non-existent user ID.
     */
    @Test
    public void changeUsernameNonExistentUserId() {
        when(userRepository.existsByUsername("newname")).thenReturn(false);
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        UserIdException ex = assertThrows(UserIdException.class,
                () -> userService.changeUsername(100L, "newname"));
        assertEquals("User with ID 100 not found!", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    /**
     * Test for successful changing of the password
     */
    @Test
    public void changePassword() {
        User fakeUser = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.encode("NewPassword1")).thenReturn("NewHash");

        userService.changePassword(1L, "NewPassword1");

        verify(fakeUser, times(1)).setPassword("NewHash");
        verify(userRepository, times(1)).save(same(fakeUser));
    }

    /**
     * Test for changing the password with incorrect user ID.
     */
    @Test
    public void changePasswordIncorrectUserId() {
        UserIdException ex = assertThrows(UserIdException.class,
                () -> userService.changePassword(0L, "NewPassword1"));
        assertEquals("User ID 0 is incorrect!", ex.getMessage());
        verify(userRepository, never()).findById(anyLong());
    }

    /**
     * Test for changing the password with non-existent user ID.
     */
    @Test
    public void changePasswordNonExistentUserId() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        UserIdException ex = assertThrows(UserIdException.class,
                () -> userService.changePassword(100L, "NewPassword1"));
        assertEquals("User with ID 100 not found!", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

}
