package me.zedaster.authservice.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO that contains only username
 */
@Data
@AllArgsConstructor
public class UsernameDto {
    /**
     * The username
     */
    private String username;
}
