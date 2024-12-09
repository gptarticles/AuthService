package me.zedaster.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity (stored in the database).
 */
@Entity
@Table(name = "users")
// Postgres automatically creates B-tree indexes for unique fields (for username and email in the case)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    private String username;

    private String password;

    @Column(unique=true)
    private String email;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private UserRoleEntity role;
}
