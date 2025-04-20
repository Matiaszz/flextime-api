package dev.matias.flextime.api.services;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.utils.CookieOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class TokenService {
    @Value("${api.security.jwt.secret}")
    private String secret;

    public String generateUserToken(User user) {
        if (secret == null || secret.isBlank()) {
            throw new RuntimeException("Secret is not set! Check the environment variables.");
        }

        try {
            log.info("Generating token for user...");
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            var token = JWT.create()
                    .withSubject(user.getEmail())
                    .withClaim("id", user.getId().toString())
                    .withClaim("role", user.getRole().toString())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 86400000 * 2))
                    .sign(algorithm);

            log.info("Generated token for user: {}", token);
            return token;

        } catch (Exception e) {
            log.error("Error generating token for user: {}", e.getMessage());
            return null;
        }
    }

    public String generateCompanyToken(Company company) {
        if (secret == null || secret.isBlank()) {
            throw new RuntimeException("Secret is not set! Check the environment variables.");
        }

        try {
            log.info("Generating token for company...");
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            var token = JWT.create()
                    .withSubject(company.getUsername())
                    .withClaim("id", company.getId().toString())
                    .withClaim("role", company.getRole().toString())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 86400000 * 2))
                    .sign(algorithm);

            log.info("Generated token for company: {}", token);
            return token;

        } catch (Exception e) {
            log.error("Error generating token for company: {}", e.getMessage());
            return null;
        }
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

}
