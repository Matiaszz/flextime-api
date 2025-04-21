package dev.matias.flextime.api.responses;

import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.CompanyRole;
import dev.matias.flextime.api.dtos.WorkerDTO;

import java.util.List;

public record CompanyResponse(
        String id,
        String username,
        String email,
        String name,
        String description,
        CompanyRole role,
        boolean enabled,
        List<WorkerDTO> workers
) {
   public CompanyResponse(Company company){
        this(
                company.getId().toString(),
                company.getUsername(),
                company.getEmail(),
                company.getName(),
                company.getDescription(),
                company.getRole(),
                company.isEnabled(),
                company.getWorkers() != null ? company.getWorkers().stream().map(WorkerDTO::new).toList() : null

        );
    }
}