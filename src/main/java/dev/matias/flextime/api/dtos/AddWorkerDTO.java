package dev.matias.flextime.api.dtos;

import jakarta.validation.constraints.NotNull;

public record AddWorkerDTO(@NotNull String email) {
}
