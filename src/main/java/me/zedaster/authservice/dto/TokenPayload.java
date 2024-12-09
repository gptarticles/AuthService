package me.zedaster.authservice.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.zedaster.authservice.model.Role;

/**
 * Payload of access or refresh JWT token.
 */
@Getter
@EqualsAndHashCode(of = "userId")
public class TokenPayload {
    /**
     * ID of the user
     */
    private final long userId;

    /**
     * Username of the user.
     */
    @Setter
    private String username;

    /**
     * Role of the user.
     */
    @Setter
    private Role role;

    public TokenPayload(long userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public TokenPayload(String sub, String username, Role role) {
        this.userId = Long.parseLong(sub);
        this.username = username;
        this.role = role;
    }

    public TokenPayload copy() {
        return new TokenPayload(userId, username, role);
    }
}
