package me.zedaster.authservice.security;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Objects;

/**
 * User details (from Spring Security) with an ID.
 */
@Getter
public class IdUserDetails extends User {

    private static final boolean ENABLED = true;
    private static final boolean ACCOUNT_NON_EXPIRED = true;
    private static final boolean ACCOUNT_NON_LOCKED = true;
    private static final boolean CREDENTIALS_NON_EXPIRED = false;

    public IdUserDetails(long userId, String username, String password) {
        super(username, password, ENABLED, ACCOUNT_NON_EXPIRED, CREDENTIALS_NON_EXPIRED, ACCOUNT_NON_LOCKED,
                Collections.emptyList());
        this.id = userId;
    }

    /**
     * The id of the user.
     */
    private final long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IdUserDetails that = (IdUserDetails) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
