package dev.matias.flextime.api.services;

import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.CompanyRole;
import dev.matias.flextime.api.dtos.CompanyRegisterDTO;
import dev.matias.flextime.api.repositories.CompanyRepository;
import dev.matias.flextime.api.responses.CompanyResponse;
import dev.matias.flextime.api.utils.CookieOptions;
import dev.matias.flextime.api.utils.ObjectBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyService implements UserDetailsService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CookieOptions cookieOptions;

    @Autowired
    private ObjectBuilder objectBuilder;

    public Company fromDTO(CompanyRegisterDTO dto) {
        return objectBuilder.companyFromDTO(dto);
    }

    public ResponseEntity<CompanyResponse> generateTokenAndCreateCookie(Company company) {
        String token = tokenService.generateCompanyToken(company);
        ResponseCookie cookie = tokenService.createCookie(token, "companyToken", cookieOptions);

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new CompanyResponse(company));
    }

    public Company getLoggedCompany() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return companyRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Company not authenticated");
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return companyRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Company not found with username: " + username));
    }
}
