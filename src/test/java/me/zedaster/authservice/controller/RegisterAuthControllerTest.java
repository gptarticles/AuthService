package me.zedaster.authservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for register method in {@link AuthController}
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegisterAuthControllerTest {
    /**
     * Mock MVC object for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Test for correct usernames.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void correctUsernameTest() throws Exception {
        String[] correctUsernames = {
                "a".repeat(3),
                "a".repeat(32),
                "a__",
                "a..",
                "a.b",
                "a_b",
                "ab1",
                "a1b",
                "Abc",
                "aBc",
                "abC"
        };

        int i = 1;
        for (String username : correctUsernames) {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(("{\"username\": \"%s\", " +
                                      "\"password\": \"Password1!\", " +
                                      "\"email\": \"user%d@example.com\"}").formatted(username, i)))
                    .andExpect(status().isOk());
            i++;
        }
    }

    /**
     * Test for usernames that are too short or too long.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void incorrectSizeUsernameTest() throws Exception {
        String[] wrongUsernames = {"", "a", "ab", "a".repeat(33)};

        for (String username : wrongUsernames) {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(("{\"username\": \"%s\", " +
                                      "\"password\": \"Password1!\", " +
                                      "\"email\": \"user@example.com\"}").formatted(username)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.*", hasSize(2)))
                    .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                    .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                    .andExpect(jsonPath("$.errorsByField.username").value("Username does not meet the requirements!"));
        }
    }

    /**
     * Test for usernames that contain incorrect characters.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void incorrectCharsUsernameTest() throws Exception {
        String[] wrongUsernames = {"___", "   ", "...", "1nick", "_nick", ".nick", "nick!name", "nick-name",
                "nick&name", "nick$name", " nick", "nick name", "nick "};

        for (String username : wrongUsernames) {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(("{\"username\": \"%s\", " +
                                      "\"password\": \"Password1!\", " +
                                      "\"email\": \"user@example.com\"}").formatted(username)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.*", hasSize(2)))
                    .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                    .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                    .andExpect(jsonPath("$.errorsByField.username").value("Username does not meet the requirements!"));
        }
    }

    /**
     * Test for a null username.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void nullUsernameTest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"username\": null, " +
                                  "\"password\": \"Password1!\", " +
                                  "\"email\": \"user@example.com\"}")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                .andExpect(jsonPath("$.errorsByField.username").value("Username must be not null!"));
    }

    /**
     * Test for correct passwords.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void correctPasswordTest() throws Exception {
        String[] correctPasswords = {
                "Minminm8",
                "A1" + "b".repeat(126),
                "aB1!?@#$%^&*_-+()[]{}></\\\\\\\\|\\\\\\\"'.,:;", // A lot of backslashes to be supported by JSON
        };

        int i = 1;
        for (String password : correctPasswords) {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(("{\"username\": \"user%d\", " +
                                      "\"password\": \"%s\", " +
                                      "\"email\": \"user%d@example.com\"}").formatted(i, password, i)))
                    .andExpect(status().isOk());
            i++;
        }
    }

    /**
     * Test for passwords that are too short or too long.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void incorrectSizePasswordTest() throws Exception {
        String[] wrongPasswords = {"", "a".repeat(7), "a".repeat(129)};

        int i = 1;
        for (String password : wrongPasswords) {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(("{\"username\": \"user%d\", " +
                                      "\"password\": \"%s\", " +
                                      "\"email\": \"user%d@example.com\"}").formatted(i, password, i)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.*", hasSize(2)))
                    .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                    .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                    .andExpect(jsonPath("$.errorsByField.password").value("Password does not meet the requirements!"));
            i++;
        }
    }

    /**
     * Test for passwords that contain incorrect characters.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void incorrectCharsPasswordTest() throws Exception {
        String[] wrongPasswords = {
                "password", // No uppercase letter
                "PASSWORD", // No lowercase letter
                "Password", // No numeral
                "pass word1", // Contains space
                "Password1№", // Contains disallowed special character
                "Пароль12", // Contains Cyrillic letters
        };

        int i = 1;
        for (String password : wrongPasswords) {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(("{\"username\": \"user%d\", " +
                                      "\"password\": \"%s\", " +
                                      "\"email\": \"user%d@example.com\"}").formatted(i, password, i)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.*", hasSize(2)))
                    .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                    .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                    .andExpect(jsonPath("$.errorsByField.password").value("Password does not meet the requirements!"));
            i++;
        }
    }

    /**
     * Test for a null password.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void nullPasswordTest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"username\": \"user1\", " +
                                  "\"password\": null, " +
                                  "\"email\": \"user1@example.com\"}")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                .andExpect(jsonPath("$.errorsByField.password").value("Password must be not null!"));
    }

    /**
     * Test for an empty email.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void emptyEmailTest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": \"\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                .andExpect(jsonPath("$.errorsByField.email").value("The email must not be empty!"));
    }

    /**
     * Test for a null email.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void nullEmailTest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": null}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                .andExpect(jsonPath("$.errorsByField.email").value("The email must not be empty!"));
    }

    /**
     * Test for incorrect emails.
     * <br/>
     * There is only one email test because the email validation is done by the
     * {@link jakarta.validation.constraints.Email} annotation.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void wrongEmailTest() throws Exception {
        String[] wrongEmails = {"a", "ab", "ab@", "ab@.", "ab@.c", "ab@c.", "ab@c.c."};

        for (String email : wrongEmails) {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(("{\"username\": \"user\", " +
                                      "\"password\": \"Password1!\", " +
                                      "\"email\": \"%s\"}").formatted(email)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.*", hasSize(2)))
                    .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                    .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                    .andExpect(jsonPath("$.errorsByField.email").value("The email is incorrect!"));
        }
    }

    /**
     * Test for registering a user with the same username.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void sameUsernameTest() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": \"email1@example.com\"}"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": \"email2@example.com\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("User with the same username already exists!"));
    }

    /**
     * Test for registering a user with the same email.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void sameEmailTest() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"user1\", \"password\": \"Password1!\", \"email\": \"same@example.com\"}"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user2\", \"password\": \"Password1!\", \"email\": \"same@example.com\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("User with the same email already exists!"));
    }
}
