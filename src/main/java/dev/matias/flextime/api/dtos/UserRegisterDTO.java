package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.domain.UserRole;

public record UserRegisterDTO(
        String username,
        UserRole role,
        String name,
        String lastName,
        String email,
        String password
) {
}
