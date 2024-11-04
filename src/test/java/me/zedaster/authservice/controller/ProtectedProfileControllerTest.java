package me.zedaster.authservice.controller;

import me.zedaster.authservice.dto.auth.JwtPairDto;
import me.zedaster.authservice.service.JwtService;
import me.zedaster.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for {@link ProtectedProfileController}.
 */
@WebMvcTest(ProtectedProfileController.class)
public class ProtectedProfileControllerTest {
    private final static String CHANGE_PASS_URL = "/protected/profile/changeUsername";

    private final static URI CHANGE_PASS_URL_FOR_USERID_ONE = UriComponentsBuilder
            .fromUriString(CHANGE_PASS_URL)
            .queryParam("tokenPayload.sub", 1)
            .build()
            .toUri();

    /**
     * Mock MVC object for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    /**
     * Test for changing the username.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameTest() throws Exception {
        when(userService.isPasswordCorrect(1, "Password1!")).thenReturn(true);
        when(jwtService.generateTokens(1, "newname"))
                .thenReturn(new JwtPairDto("token1", "token2"));

        mockMvc.perform(post(CHANGE_PASS_URL_FOR_USERID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newUsername\": \"newname\", \"password\": \"Password1!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.accessToken").value("token1"))
                .andExpect(jsonPath("$.refreshToken").value("token2"));

        verify(userService, times(1)).isPasswordCorrect(1, "Password1!");
        verify(userService, times(1)).changeUsername(1, "newname");
    }

    /**
     * Test for changing the username with incorrect password.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameWrongPasswordTest() throws Exception {
        when(userService.isPasswordCorrect(1, "wrongpass")).thenReturn(false);

        mockMvc.perform(post(CHANGE_PASS_URL_FOR_USERID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newUsername\": \"newname\", \"password\": \"wrongpass\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("The password is incorrect!"));

        verify(userService, never()).changeUsername(anyLong(), anyString());
        verify(jwtService, never()).generateTokens(anyLong(), anyString());
    }

    /**
     * Test for changing the username with incorrect nickname.
     * It is short because the nickname validation is already tested in {@link AuthControllerTest}.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameIncorrectNickname() throws Exception {
        String[] wrongUsernames = {"", "_aa", "aa", "a".repeat(33)};

        for (String username : wrongUsernames) {
            String json = "{\"newUsername\": \"%s\", \"password\": \"Password1!\"}".formatted(username);
            testChangeUsernameWithInvalidJson(json, "newUsername",
                    "Username does not meet the requirements!");
        }

        verify(userService, never()).isPasswordCorrect(anyLong(), anyString());
    }

    /**
     * Test for changing the username with incorrect password.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameIncorrectPassword() throws Exception {
        String[] wrongPasswords = {"", "a".repeat(7), "a".repeat(129),
                "汉".repeat(8), " ".repeat(8)};

        for (String password : wrongPasswords) {
            String json = "{\"newUsername\": \"newname\", \"password\": \"%s\"}".formatted(password);
            testChangeUsernameWithInvalidJson(json, "password",
                    "Password is incorrect!");
        }

        verify(userService, never()).isPasswordCorrect(anyLong(), anyString());
    }

    @Test
    public void changeUsernameNullNickname() throws Exception {
        String json = "{\"newUsername\": null, \"password\": \"Password1!\"}";
        testChangeUsernameWithInvalidJson(json, "newUsername",
                "Username can't be null!");
        verify(userService, never()).isPasswordCorrect(anyLong(), anyString());
    }

    @Test
    public void changeUsernameNullPassword() throws Exception {
        String json = "{\"newUsername\": \"newname\", \"password\": null}";
        testChangeUsernameWithInvalidJson(json, "password", "Password can't be null!");
        verify(userService, never()).isPasswordCorrect(anyLong(), anyString());
    }

    private void testChangeUsernameWithInvalidJson(String json, String expectErrorField, String expectedErrorMsg) throws Exception {
        mockMvc.perform(post(CHANGE_PASS_URL_FOR_USERID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                .andExpect(jsonPath("$.errorsByField." + expectErrorField).value(expectedErrorMsg));
    }
}
