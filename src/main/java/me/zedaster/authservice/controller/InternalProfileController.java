package me.zedaster.authservice.controller;

import lombok.RequiredArgsConstructor;
import me.zedaster.authservice.dto.profile.UsernameDto;
import me.zedaster.authservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/username")
    public UsernameDto getUsernameById(long userId) {
        return new UsernameDto(userService.getUsername(userId));
    }
}
