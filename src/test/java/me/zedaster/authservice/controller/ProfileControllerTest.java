package me.zedaster.authservice.controller;

import me.zedaster.authservice.util.MvcTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for {@link ProfileController}.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProfileControllerTest {

    /**
     * Mock MVC object for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Utilities for testing with MockMvc.
     */
    @Autowired
    private MvcTestUtils mvcUtils;

    /**
     * Test for changing the username.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameTest() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": \"user@example.com\"}"))
                .andReturn();

        Map<String, Object> loginResultJson = mvcUtils.jsonResultToMap(registerResult);
        String accessToken = loginResultJson.get("accessToken").toString();

        mockMvc.perform(post("/profile/changeUsername")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newUsername\": \"newname\", \"password\": \"Password1!\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\": \"newname\", \"password\": \"Password1!\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\": \"user\", \"password\": \"Password1!\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The username or password are incorrect!"));
    }

    /**
     * Test for changing the username with incorrect password.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameWrongPasswordTest() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": \"user@example.com\"}"))
                .andReturn();

        Map<String, Object> registerResultJson = mvcUtils.jsonResultToMap(registerResult);
        String accessToken = registerResultJson.get("accessToken").toString();

        mockMvc.perform(post("/profile/changeUsername")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newUsername\": \"newname\", \"password\": \"wrongpass\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The password is incorrect!"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\": \"user\", \"password\": \"Password1!\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\": \"newname\", \"password\": \"Password1!\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The username or password are incorrect!"));
    }

    /**
     * Test for changing the username with incorrect nickname.
     * It is short because the nickname validation is already tested in {@link AuthControllerTest}.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameIncorrectNickname() throws Exception {
        String[] wrongUsernames = {"_aa", "aa", "a".repeat(33)};

        int i = 1;
        for (String username : wrongUsernames) {
            MvcResult registerResult = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\": \"user%d\", \"password\": \"Password1!\", \"email\": \"user%d@example.com\"}"
                                    .formatted(i, i)))
                    .andReturn();

            Map<String, Object> registerResultJson = mvcUtils.jsonResultToMap(registerResult);
            String accessToken = registerResultJson.get("accessToken").toString();

            mockMvc.perform(post("/profile/changeUsername")
                            .header("Authorization", "Bearer %s".formatted(accessToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"newUsername\": \"%s\", \"password\": \"Password1!\"}".formatted(username)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.*", hasSize(2)))
                    .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                    .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                    .andExpect(jsonPath("$.errorsByField.newUsername").value("Username does not meet the requirements!"));
            i++;
        }
    }

    /**
     * Test for changing the username if the new username is already taken.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameTaken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user1\", \"password\": \"Password1!\", \"email\": \"user@example.com\"}"));

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user2\", \"password\": \"Password2!\", \"email\": \"newname@example.com\"}"))
                .andReturn();

        Map<String, Object> registerResultJson = mvcUtils.jsonResultToMap(registerResult);
        String accessToken = registerResultJson.get("accessToken").toString();

        mockMvc.perform(post("/profile/changeUsername")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newUsername\": \"user1\", \"password\": \"Password2!\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The username is already taken!"));
    }
}
