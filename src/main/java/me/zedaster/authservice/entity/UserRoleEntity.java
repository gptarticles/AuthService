package me.zedaster.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import me.zedaster.authservice.model.Role;

/**
 * Entity for user roles. (All except {@link Role#USER} are stored in the database).
 */
@Entity
@Table(name = "user_roles")
@Data
@NoArgsConstructor
public class UserRoleEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Convert(converter = RoleConverter.class)
    private Role role;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public UserRoleEntity(Long userId, Role role) {
        this.userId = userId;
        this.role = role;
    }
}
