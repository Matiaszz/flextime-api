package dev.matias.flextime.api.repositories;

import dev.matias.flextime.api.domain.Company;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByUsername(String username);
    Optional<Company> findByName(String name);

    Optional<Company> findByEmail(@Email(message = "Email must be valid") @NotBlank(message = "Email is required") String email);
}
