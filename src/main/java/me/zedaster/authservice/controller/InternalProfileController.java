package me.zedaster.authservice.controller;

import lombok.RequiredArgsConstructor;
import me.zedaster.authservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Profile REST controller for methods for internal use
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/profile")
public class InternalProfileController {
    /**
     * Service for working with users.
     */
    private final UserService userService;

    /**
     * Returns username by user ID.
     * @param userId User ID.
     * @return JSON object with username.
     */
    @GetMapping("/{id}/username")
    public String getUsernameById(@PathVariable("id") Long userId) {
        return userService.getUsername(userId);
    }

    /**
     * Returns usernames by user IDs.
     * @param userIds List of user IDs.
     * @return List of JSON objects with username.
     */
    @GetMapping("/usernames")
    public List<String> getUsernamesByIds(@RequestParam("ids") List<Long> userIds) {
        return userService.getUsernames(userIds);
    }
}
