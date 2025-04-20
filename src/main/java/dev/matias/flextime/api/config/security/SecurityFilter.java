package dev.matias.flextime.api.config.security;

import dev.matias.flextime.api.repositories.CompanyRepository;
import dev.matias.flextime.api.repositories.UserRepository;
import dev.matias.flextime.api.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String companyToken = recoverCompanyLoginToken(request);
        String userToken = recoverUserLoginToken(request);

        if (companyToken != null && userToken != null) {
            log.warn("Both companyToken and userToken received. Ambiguous authentication.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Multiple authentication tokens provided.");
        }

        try {
            if (companyToken != null) {
                String companyUsername= tokenService.validateToken(companyToken);
                UserDetails company = companyRepository.findByUsername(companyUsername)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found (Security Filter)"));

                var authentication = new UsernamePasswordAuthenticationToken(company, null, company.getAuthorities());
                log.info("Authenticated company: {}", company.getUsername());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else if (userToken != null) {
                String userEmail = tokenService.validateToken(userToken);
                UserDetails user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found (Security Filter)"));

                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                log.info("Authenticated user: {}", user.getUsername());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String recoverUserLoginToken(HttpServletRequest request) {

        if (request.getCookies() != null){
            for (var cookie : request.getCookies()){
                if ("userToken".equalsIgnoreCase(cookie.getName())){
                    log.info("User Token received from cookie: {}", cookie.getName());
                    return cookie.getValue();
                }
            }
        }
        log.info("User Token in token recovery is null.");
        return null;
    }

    private String recoverCompanyLoginToken(HttpServletRequest request) {

        if (request.getCookies() != null){
            for (var cookie : request.getCookies()){
                if ("companyToken".equalsIgnoreCase(cookie.getName())){
                    log.info("Company Token received from cookie: {}", cookie.getName());
                    return cookie.getValue();
                }
            }
        }
        log.info("Token in company token recovery is null.");
        return null;
    }
}
