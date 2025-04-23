package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.validations.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CompanyRegisterDTO(
        @NotEmpty(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Password
        String password,

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Company name is required")
        String name,

        String description
) {
}
