package dev.matias.flextime.api.dtos;

import jakarta.validation.constraints.NotNull;

public record CompanyLoginDTO(@NotNull String username, @NotNull  String password) {
}
