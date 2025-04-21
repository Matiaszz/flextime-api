package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.domain.UserRole;

public record WorkerDTO(String name, String lastName, String email, UserRole role, String companyName) {
    public WorkerDTO(User user){
        this(
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getCompany() != null ? user.getCompany().getName() : null
        );
    }
}
