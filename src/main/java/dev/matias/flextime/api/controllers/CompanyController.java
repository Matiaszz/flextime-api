package dev.matias.flextime.api.controllers;

import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.dtos.CompanyRegisterDTO;
import dev.matias.flextime.api.repositories.CompanyRepository;
import dev.matias.flextime.api.responses.CompanyResponse;
import dev.matias.flextime.api.services.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyService companyService;

    @PostMapping("/register")
    public ResponseEntity<CompanyResponse> register(@RequestBody @Valid CompanyRegisterDTO dto){
        if (companyRepository.findByUsername(dto.username()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company username already exists.");
        }

        if (companyRepository.findByName(dto.name()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company with name: " + dto.name() + " already exists.");
        }

        if (companyRepository.findByEmail(dto.email()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company with email: " + dto.email() + " already exists.");
        }

        Company company = companyService.fromDTO(dto);
        companyRepository.save(company);
        return companyService.generateTokenAndCreateCookie(company);
    }
}
