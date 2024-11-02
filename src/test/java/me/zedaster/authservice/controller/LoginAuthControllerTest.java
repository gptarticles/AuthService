package me.zedaster.authservice.controller;

import jakarta.annotation.Nullable;
import me.zedaster.authservice.service.JwtService;
import me.zedaster.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for login method in {@link AuthController}
 */
@WebMvcTest(AuthController.class)
public class LoginAuthControllerTest {
    /**
     * Mock MVC object for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock service for getting users
     */
    @MockBean
    private UserService userService;

    /**
     * Stub just for running the tests
     */
    @MockBean
    private JwtService jwtService;

    // Right login tested in AuthControllerTest.java

    /**
     * Test for logging in with correct, but wrong credentials.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void wrongLogin() throws Exception {
        when(userService.getUser(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\": \"user\", \"password\": \"Password1!\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The username or password are incorrect!"));

        verify(userService, times(1)).getUser(argThat(credentials ->
                credentials.getUsernameOrEmail().equals("user") && credentials.getPassword().equals("Password1!")));
    }

    /**
     * Test for logging in with too short username or email.
     */
    @Test
    public void tooShortUsernameOrEmail() throws Exception {
        testWithIncorrectUsernameOrEmail("a");
        testWithIncorrectUsernameOrEmail("aa");
    }

    /**
     * Test for logging in with too long username or email.
     */
    @Test
    public void tooLongUsernameOrEmail() throws Exception {
        // An email address must not exceed 254 characters
        testWithIncorrectUsernameOrEmail("a".repeat(255));
    }

    /**
     * Test for logging in with wrong characters in username or email.
     */
    @Test
    public void wrongCharsUsernameOrEmail() throws Exception {
        testWithIncorrectUsernameOrEmail("中文中文中文中文中文中文中文中文");
    }

    /**
     * Test for logging in with empty username or email.
     */
    @Test
    public void emptyUsernameOrEmail() throws Exception {
        testWithIncorrectUsernameOrEmail("");
    }

    /**
     * Test for logging in with null username or email.
     */
    @Test
    public void nullUsernameOrEmail() throws Exception {
        testWithIncorrectUsernameOrEmail(null);
    }

    /**
     * Test for logging in with too short password.
     */
    @Test
    public void tooShortPassword() throws Exception {
        testWithIncorrectPassword("aA1" + "a".repeat(4)); // 7 characters in total
    }

    /**
     * Test for logging in with too long password.
     */
    @Test
    public void tooLongPassword() throws Exception {
        testWithIncorrectPassword("aA1" + "a".repeat(126)); // 129 characters in total
    }

    /**
     * Test for logging in with wrong characters in password.
     */
    @Test
    public void wrongCharsPassword() throws Exception {
        testWithIncorrectPassword("aA1" + "中".repeat(5));
    }

    /**
     * Test for logging in with empty password.
     */
    @Test
    public void emptyPassword() throws Exception {
        testWithIncorrectPassword("");
    }

    /**
     * Test for logging in with null password.
     */
    @Test
    public void nullPassword() throws Exception {
        testWithIncorrectPassword(null);
    }

    /**
     * Test for logging in with incorrect username or email.
     * @param usernameOrEmail Username or email.
     */
    private void testWithIncorrectUsernameOrEmail(@Nullable String usernameOrEmail) throws Exception {
        testWithIncorrectCredentials(usernameOrEmail, "Password1!");
    }

    /**
     * Test for logging in with incorrect password.
     * @param password Password.
     */
    private void testWithIncorrectPassword(@Nullable String password) throws Exception {
        testWithIncorrectCredentials("user", password);
    }

    /**
     * Test for logging in with incorrect credentials.
     * @param usernameOrEmail Username or email.
     * @param password Password.
     */
    private void testWithIncorrectCredentials(@Nullable String usernameOrEmail, @Nullable String password) throws Exception {
        String jsonUsername = getJsonString(usernameOrEmail);
        String jsonPassword = getJsonString(password);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\": %s, \"password\": %s}"
                                .formatted(jsonUsername, jsonPassword)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The username or password are incorrect!"));

        verify(userService, never()).getUser(any());
    }

    /**
     * Returns a JSON string with the value or null.
     * @param value Nullable value to put in the JSON string.
     * @return JSON string or null.
     */
    private String getJsonString(@Nullable String value) {
        return value == null ? "null" : "\"%s\"".formatted(value);
    }


}
