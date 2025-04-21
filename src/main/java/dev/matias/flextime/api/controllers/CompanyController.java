package dev.matias.flextime.api.controllers;

import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.dtos.CompanyLoginDTO;
import dev.matias.flextime.api.dtos.CompanyRegisterDTO;
import dev.matias.flextime.api.repositories.CompanyRepository;
import dev.matias.flextime.api.responses.CompanyResponse;
import dev.matias.flextime.api.responses.UserResponse;
import dev.matias.flextime.api.services.CompanyService;
import dev.matias.flextime.api.services.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    @Qualifier("companyAuthenticationManager")
    private AuthenticationManager authenticationManager;




    @PostMapping("/register")
    public ResponseEntity<CompanyResponse> register(@RequestBody @Valid CompanyRegisterDTO dto, HttpServletRequest request){
        if (tokenService.hasUserToken(request)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An user is already authenticated. Please logout first.");
        }

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

    @PostMapping("/login")
    public ResponseEntity<CompanyResponse> login(@RequestBody @Valid CompanyLoginDTO dto, HttpServletRequest request){
        if (tokenService.hasUserToken(request)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An user is already authenticated. Please logout first.");
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(usernamePassword);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password or company is disabled " + e.getMessage());
        }
        return companyService.generateTokenAndCreateCookie((Company) authentication.getPrincipal());
    }

    @GetMapping
    public ResponseEntity<CompanyResponse> getLoggedCompany(HttpServletRequest request){
        if (tokenService.hasUserToken(request)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An user is already authenticated. Please logout first.");
        }
        return ResponseEntity.ok(new CompanyResponse(companyService.getLoggedCompany()));

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        if (tokenService.hasUserToken(request)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An user is already authenticated. Please logout first.");
        }
        ResponseCookie cookie = ResponseCookie.from("companyToken", "").httpOnly(true).secure(true)
                .sameSite("None").path("/").maxAge(0).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
