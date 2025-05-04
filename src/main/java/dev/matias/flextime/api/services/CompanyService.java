package dev.matias.flextime.api.services;

import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.domain.UserRole;
import dev.matias.flextime.api.dtos.CompanyRegisterDTO;
import dev.matias.flextime.api.repositories.CompanyRepository;
import dev.matias.flextime.api.repositories.UserRepository;
import dev.matias.flextime.api.responses.CompanyResponse;
import dev.matias.flextime.api.utils.CookieOptions;
import dev.matias.flextime.api.utils.ObjectBuilder;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyService implements UserDetailsService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

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

    @Transactional
    public void addWorker(@NotNull String email, String companyName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not enabled");
        }

        if (user.getRole() == UserRole.WORKER && user.getCompany() == company) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This user already works in this company.");
        }

        if (user.getRole() == UserRole.WORKER && user.getCompany() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user already works for another company.");
        }

        if (user.getRole() == UserRole.WORKER) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "User is WORKER but has no company. This should never happen.");
        }

        if (user.getCompany() != null && user.getRole() == UserRole.CLIENT) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "User is CLIENT but has a company. This should never happen.");
        }

        user.setRole(UserRole.WORKER);
        user.setCompany(company);
        company.getWorkers().add(user);

       userRepository.save(user);
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
