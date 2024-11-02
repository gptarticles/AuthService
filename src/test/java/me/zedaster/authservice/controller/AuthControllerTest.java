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
 * General tests for {@link AuthController}.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerTest {
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
     * Create a new user, log in, verify and refresh the tokens.
     */
    @Test
    public void createAndLoginUserAndVerifyAndRefreshToken() throws Exception {
        // Create an account
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": \"user@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andReturn();

        // Get tokens after registration
        Map<String, Object> registerResultJson = mvcUtils.jsonResultToMap(registerResult);
        String regAccessToken = registerResultJson.get("accessToken").toString();
        String regRefreshToken = registerResultJson.get("refreshToken").toString();

        // Verifying the access token after registration
        mockMvc.perform(post("/auth/verifyToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accessToken\": \"%s\"}".formatted(regAccessToken)))
                .andExpect(status().isOk());

        // Refresh the access token after registration
        MvcResult refreshResult = mockMvc.perform(post("/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"%s\"}".formatted(regRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andReturn();

        // Get the refreshed access and refresh tokens
        Map<String, Object> refreshResultJson = mvcUtils.jsonResultToMap(refreshResult);
        String refAccessToken = refreshResultJson.get("accessToken").toString();
        String refRefreshToken = refreshResultJson.get("refreshToken").toString();

        // Assert the refreshed access token
        mockMvc.perform(post("/auth/verifyToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accessToken\": \"%s\"}".formatted(refAccessToken)))
                .andExpect(status().isOk());

        // Assert the refreshed refresh token
        mockMvc.perform(post("/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"%s\"}".formatted(refRefreshToken)))
                .andExpect(status().isOk());

        // Log in into the created account by email
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\": \"user@example.com\", \"password\": \"Password1!\"}"))
                .andExpect(status().isOk());

        // Log in into the created account by username
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\": \"user\", \"password\": \"Password1!\"}"))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> loginResultJson = mvcUtils.jsonResultToMap(loginResult);
        String accessToken = loginResultJson.get("accessToken").toString();
        String refreshToken = loginResultJson.get("refreshToken").toString();

        // Verifying the access token after login
        mockMvc.perform(post("/auth/verifyToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accessToken\": \"%s\"}".formatted(accessToken)))
                .andExpect(status().isOk());

        // Verifying the refresh token after login
        mockMvc.perform(post("/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"%s\"}".formatted(refreshToken)))
                .andExpect(status().isOk());
    }

    /**
     * Test for verifying a wrong access token.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void verifyWrongToken() throws Exception {
        final String expiredToken = "eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidXNlciIsImlhdCI6MTcyODM4" +
                                    "NzI1OSwiZXhwIjoxNzI4Mzg3NTU5fQ.GtQA2wZIf0QpPRUzCyRn8Upl0FZP7wMMUFZKFSZph49tP-" +
                                    "IoUXykLE3hpp0z6aTt";

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": \"user@example.com\"}"));

        mockMvc.perform(post("/auth/verifyToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accessToken\": \"%s\"}".formatted(expiredToken)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The access token is invalid!"));
    }

    /**
     * Test for refreshing a wrong refresh token.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void refreshWrongToken() throws Exception {
        final String expiredToken = "eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidXNlciIsImlhdCI6MTcyODM4ODA" +
                                    "1NiwiZXhwIjoxNzI4Mzg4MTE2fQ.euUv4K8s-otLoazdMO-bixNmqdJU0Uw7b5rjEDUzTxG05utJeW0x" +
                                    "qOGYFNbKR0Lo";

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"user\", \"password\": \"Password1!\", \"email\": \"user@example.com\"}"));

        mockMvc.perform(post("/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"%s\"}".formatted(expiredToken)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The refresh token is invalid!"));
    }

}