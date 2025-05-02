package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.domain.UserRole;
import dev.matias.flextime.api.validations.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRegisterDTO(
        @NotNull
        String username,
        UserRole role,
        @NotNull
        String name,
        @NotNull
        String lastName,
        @NotNull
        @Email
        String email,
        @NotNull
        @Password
        String password
) {
}
