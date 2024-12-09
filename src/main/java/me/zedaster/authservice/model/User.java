package me.zedaster.authservice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.zedaster.authservice.entity.UserEntity;

/**
 * User entity (stored in the database).
 */

// Postgres automatically creates B-tree indexes for unique fields (for username and email in the case)
@Getter
@EqualsAndHashCode(of = "id")
public class User {
    private final long id;

    private final String username;

    private final String email;

    private final Role role;

    private User(long id, String username, String email, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public static User fromEntityAndRole(UserEntity userEntity, Role role) {
        return new User(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), role);
    }
}
