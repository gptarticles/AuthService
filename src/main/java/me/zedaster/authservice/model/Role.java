package me.zedaster.authservice.model;

import lombok.Getter;

@Getter
public enum Role {
    USER(0),
    MODERATOR(1);

    private static final Role[] values = values();

    private final int id;

    Role(int id) {
        this.id = id;
    }

    public static Role getById(int id) {
        return values[id];
    }
}
