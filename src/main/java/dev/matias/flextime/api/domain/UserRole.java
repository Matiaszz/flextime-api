package dev.matias.flextime.api.domain;

import lombok.Getter;

import java.util.List;

@Getter
public enum UserRole {
    WORKER("ROLE_WORKER"),
    CLIENT("ROLE_CLIENT");

    private final String role;

    UserRole(String role){
        this.role = role;
    }

    public List<String> getPermissions() {
        return switch (this) {
            case WORKER -> List.of("ROLE_WORKER", "ROLE_CLIENT");
            case CLIENT -> List.of("ROLE_CLIENT");
        };
    }

}