package dev.matias.flextime.api.responses;

import dev.matias.flextime.api.domain.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

public record UserResponse(
        String id,
        String username,
        String role,
        String name,
        String lastName,
        String email,
        String description,
        CompanyMinimalResponse company,
        String profileImageURL,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean enabled,
        Collection<? extends GrantedAuthority> authorities
) {
    public UserResponse(User user) {
        this(user.getId().toString(),
                user.getUsername(),
                user.getRole().toString(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getDescription(),
                user.getCompany() == null ? null : new CompanyMinimalResponse(user.getCompany()),
                user.getProfileImageURL(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isEnabled(),
                user.getAuthorities());
    }
}
