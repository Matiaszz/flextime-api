package dev.matias.flextime.api.services;

import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.CompanyRole;
import dev.matias.flextime.api.dtos.CompanyRegisterDTO;
import dev.matias.flextime.api.responses.CompanyResponse;
import dev.matias.flextime.api.utils.CookieOptions;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.Token;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CookieOptions cookieOptions;

    public Company fromDTO(CompanyRegisterDTO dto) {
        return Company.builder()
                .username(dto.username())
                .password(passwordEncoder.encode(dto.password()))
                .name(dto.name())
                .email(dto.email())
                .description(dto.description())
                .role(CompanyRole.COMPANY)
                .enabled(true)
                .build();
    }

    public ResponseEntity<CompanyResponse> generateTokenAndCreateCookie(Company company) {
        String token = tokenService.generateCompanyToken(company);
        ResponseCookie cookie = tokenService.createCookie(token, "companyToken", cookieOptions);

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new CompanyResponse(company));
    }

    public Boolean hasCompanyToken(HttpServletRequest request){
        String token = null;
        for (Cookie cookie : request.getCookies()){
            if (cookie.getName().equalsIgnoreCase("companyToken")){
                token = cookie.getValue();
            }
        }
        return token != null;
    }
}
