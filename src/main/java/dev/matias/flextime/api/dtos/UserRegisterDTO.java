package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.domain.UserRole;
import dev.matias.flextime.api.validations.Password;
import jakarta.validation.constraints.Email;

public record UserRegisterDTO(
        String username,
        UserRole role,
        String name,
        String lastName,
        @Email
        String email,
        @Password
        String password
) {
}
