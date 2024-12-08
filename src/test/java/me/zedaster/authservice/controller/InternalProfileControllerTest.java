package me.zedaster.authservice.controller;

import me.zedaster.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link InternalProfileController}
 */
@WebMvcTest(InternalProfileController.class)
public class InternalProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void getUsernameById() throws Exception {
        when(userService.getUsername(1L)).thenReturn("barbra.streisand");
        mockMvc.perform(get("/internal/profile/1/username"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("barbra.streisand"));
    }

    @Test
    public void getUsernamesByIds() throws Exception {
        when(userService.getUsernames(List.of(1L, 2L))).thenReturn(List.of("barbra.streisand", "john.doe"));
        mockMvc.perform(get("/internal/profile/usernames?ids=1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("[0]").value("barbra.streisand"))
                .andExpect(jsonPath("[1]").value("john.doe"));
    }
}
