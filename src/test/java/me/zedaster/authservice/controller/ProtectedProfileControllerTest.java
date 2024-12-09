package me.zedaster.authservice.controller;

import me.zedaster.authservice.dto.TokenPayload;
import me.zedaster.authservice.dto.auth.JwtPairDto;
import me.zedaster.authservice.model.User;
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
    private final static String CHANGE_USERNAME_URL = "/protected/profile/changeUsername";

    private final static String CHANGE_PASS_URL = "/protected/profile/changePassword";

    private final static URI CHANGE_USERNAME_URL_FOR_USERID_ONE = UriComponentsBuilder
            .fromUriString(CHANGE_USERNAME_URL)
            .queryParam("tokenPayload.sub", 1)
            .queryParam("tokenPayload.username", "test")
            .queryParam("tokenPayload.role", "USER")
            .build()
            .toUri();

    private final static URI CHANGE_PASS_URL_FOR_USERID_ONE = UriComponentsBuilder
            .fromUriString(CHANGE_PASS_URL)
            .queryParam("tokenPayload.sub", 1)
            .queryParam("tokenPayload.username", "test")
            .queryParam("tokenPayload.role", "USER")
            .build()
            .toUri();

    private final static String[] WRONG_SHALLOW_PASSWORDS = {"", "a".repeat(7), "a".repeat(129),
            "汉".repeat(8), " ".repeat(8)};

    private final static String[] WRONG_EXACT_PASSWORDS = {"", "a".repeat(5) + "A1", "a".repeat(127) + "A1",
            "汉".repeat(5) + "aA1", " ".repeat(8), "a".repeat(8), "aA".repeat(4),
            "a1".repeat(4), "A1".repeat(4)};


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
        when(jwtService.generateTokens(any(TokenPayload.class)))
                .thenReturn(new JwtPairDto("token1", "token2"));

        mockMvc.perform(post(CHANGE_USERNAME_URL_FOR_USERID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newUsername\": \"newname\", \"password\": \"Password1!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.accessToken").value("token1"))
                .andExpect(jsonPath("$.refreshToken").value("token2"));

        verify(userService, times(1)).isPasswordCorrect(1, "Password1!");
        verify(userService, times(1)).changeUsername(1, "newname");
        verify(jwtService, times(1)).generateTokens((TokenPayload) argThat(payload ->
                ((TokenPayload) payload).getUserId() == 1 &&
                ((TokenPayload) payload).getUsername().equals("newname")));
    }

    /**
     * Test for changing the username with incorrect password.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void changeUsernameWrongPasswordTest() throws Exception {
        when(userService.isPasswordCorrect(1, "wrongpass")).thenReturn(false);

        mockMvc.perform(post(CHANGE_USERNAME_URL_FOR_USERID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newUsername\": \"newname\", \"password\": \"wrongpass\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.message").value("The password is incorrect!"));

        verify(userService, never()).changeUsername(anyLong(), anyString());
        verify(jwtService, never()).generateTokens(any(TokenPayload.class));
        verify(jwtService, never()).generateTokens(any(User.class));
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
        for (String password : WRONG_SHALLOW_PASSWORDS) {
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

    @Test
    public void changePasswordTest() throws Exception {
        when(userService.isPasswordCorrect(1L, "Password1!")).thenReturn(true);
        when(userService.getUsername(1L)).thenReturn("test");
        when(jwtService.generateTokens(any(TokenPayload.class)))
                .thenReturn(new JwtPairDto("token1", "token2"));

        mockMvc.perform(post(CHANGE_PASS_URL_FOR_USERID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\": \"newPassword1\", \"oldPassword\": \"Password1!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.accessToken").value("token1"))
                .andExpect(jsonPath("$.refreshToken").value("token2"));

        verify(userService, times(1)).isPasswordCorrect(1, "Password1!");
        verify(userService, times(1)).changePassword(1, "newPassword1");
        verify(jwtService, times(1)).generateTokens((TokenPayload) argThat(payload ->
                ((TokenPayload) payload).getUserId() == 1 &&
                ((TokenPayload) payload).getUsername().equals("test")));
    }

    @Test
    public void changePasswordWithIncorrectOldOne() throws Exception {
        when(userService.isPasswordCorrect(1, "Password1!")).thenReturn(false);

        mockMvc.perform(post(CHANGE_PASS_URL_FOR_USERID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\": \"newPassword1\", \"oldPassword\": \"Password1!\"}"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.message").value("The password is incorrect!"));

        verify(userService, never()).changePassword(anyLong(), anyString());
    }

    @Test
    public void changePasswordToNull() throws Exception {
        String json = "{\"newPassword\": null, \"oldPassword\": \"Password1\"}";
        testChangePasswordWithInvalidJson(json, "newPassword", "New password can't be null!");
        verify(userService, never()).isPasswordCorrect(anyLong(), anyString());
    }

    @Test
    public void changeNullPassword() throws Exception {
        String json = "{\"newPassword\": \"Password1\", \"oldPassword\": null}";
        testChangePasswordWithInvalidJson(json, "oldPassword", "Old password can't be null!");
        verify(userService, never()).isPasswordCorrect(anyLong(), anyString());
    }

    @Test
    public void changePasswordToIncorrectOne() throws Exception {
        for (String newPassword : WRONG_EXACT_PASSWORDS) {
            String json = "{\"newPassword\": \"%s\", \"oldPassword\": \"Password1\"}".formatted(newPassword);
            testChangePasswordWithInvalidJson(json, "newPassword", "Password does not meet the requirements!");
        }
        verify(userService, never()).isPasswordCorrect(anyLong(), anyString());
    }

    @Test
    public void changeIncorrectPassword() throws Exception {
        for (String oldPassword : WRONG_SHALLOW_PASSWORDS) {
            String json = "{\"newPassword\": \"Password1\", \"oldPassword\": \"%s\"}".formatted(oldPassword);
            testChangePasswordWithInvalidJson(json, "oldPassword", "Password is incorrect!");
        }
        verify(userService, never()).isPasswordCorrect(anyLong(), anyString());
    }

    private void testChangeUsernameWithInvalidJson(String json, String expectErrorField, String expectedErrorMsg) throws Exception {
        testPostWithInvalidJson(CHANGE_USERNAME_URL_FOR_USERID_ONE, json, expectErrorField, expectedErrorMsg);
    }

    private void testChangePasswordWithInvalidJson(String json, String expectErrorField, String expectedErrorMsg) throws Exception {
        testPostWithInvalidJson(CHANGE_PASS_URL_FOR_USERID_ONE, json, expectErrorField, expectedErrorMsg);
    }

    private void testPostWithInvalidJson(URI uri, String json, String expectErrorField, String expectedErrorMsg) throws Exception {
        mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.message").value("Error validating the request data!"))
                .andExpect(jsonPath("$.errorsByField.*", hasSize(1)))
                .andExpect(jsonPath("$.errorsByField." + expectErrorField).value(expectedErrorMsg));
    }
}
