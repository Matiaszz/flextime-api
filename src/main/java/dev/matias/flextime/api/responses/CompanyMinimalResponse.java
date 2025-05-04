package dev.matias.flextime.api.responses;

import dev.matias.flextime.api.domain.Company;

public record CompanyMinimalResponse(
        String id,
        String name,
        String email
) {
    public CompanyMinimalResponse(Company company) {
        this(company.getId().toString(), company.getName(), company.getEmail());
    }
}
