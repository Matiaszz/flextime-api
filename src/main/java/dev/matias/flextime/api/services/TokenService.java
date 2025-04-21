package dev.matias.flextime.api.services;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.responses.UserResponse;
import dev.matias.flextime.api.utils.CookieOptions;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class TokenService {
    @Value("${api.security.jwt.secret}")
    private String secret;

    @Autowired
    private CookieOptions cookieOptions;

    private String generateToken(String subject, String id, String role) {
        if (secret == null || secret.isBlank()) {
            throw new RuntimeException("Secret is not set! Check the environment variables.");
        }

        try {
            log.info("Generating token for subject: {}", subject);
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            return JWT.create()
                    .withSubject(subject)
                    .withClaim("id", id)
                    .withClaim("role", role)
                    .withExpiresAt(new Date(System.currentTimeMillis() + 86400000 * 2))
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("Error generating token: {}", e.getMessage());
            return null;
        }
    }

    public String generateUserToken(User user) {
        return generateToken(user.getEmail(), user.getId().toString(), user.getRole().toString());
    }

    public String generateCompanyToken(Company company) {
        return generateToken(company.getUsername(), company.getId().toString(), company.getRole().toString());
    }

    public ResponseEntity<UserResponse> generateUserTokenAndCreateCookie(User user) {
        String token = generateUserToken(user);
        ResponseCookie cookie = createCookie(token, "userToken", cookieOptions);

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new UserResponse(user));
    }

    public String validateToken(String token) {
        try {
            log.info("Validating token...");
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return null;
        }
    }

    public ResponseCookie createCookie(String token, String name, CookieOptions opts) {
        return ResponseCookie.from(name, token)
                .httpOnly(opts.httpOnly)
                .secure(opts.secure)
                .sameSite(opts.sameSite)
                .path(opts.path)
                .maxAge(opts.maxAge)
                .build();
    }

    public boolean hasUserToken(HttpServletRequest request){
        return hasToken(request, "userToken");
    }

    public boolean hasCompanyToken(HttpServletRequest request){
        return hasToken(request, "companyToken");
    }

    private boolean hasToken(HttpServletRequest request, String cookieName){
        if (request.getCookies() == null) return false;

        for (Cookie cookie : request.getCookies()){
            if (cookie.getName().equalsIgnoreCase(cookieName)){
                return true;
            }
        }
        return false;
    }

}
