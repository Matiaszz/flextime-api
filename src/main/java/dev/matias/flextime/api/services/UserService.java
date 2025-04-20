package dev.matias.flextime.api.services;

import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.domain.UserRole;
import dev.matias.flextime.api.dtos.UserRegisterDTO;
import dev.matias.flextime.api.repositories.UserRepository;
import dev.matias.flextime.api.responses.UserResponse;
import dev.matias.flextime.api.utils.CookieOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CookieOptions cookieOptions;

    @Autowired
    private UserRepository userRepository;

    public User fromDTO(UserRegisterDTO dto) {
        return User.builder()
                .username(dto.username())
                .role(dto.role() != null ? dto.role() : UserRole.CLIENT)
                .name(dto.name())
                .lastName(dto.lastName())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .enabled(true)
                .build();
    }

    public ResponseEntity<UserResponse> generateTokenAndCreateCookie(User user) {
        String token = tokenService.generateUserToken(user);
        ResponseCookie cookie = tokenService.createCookie(token, "userToken", cookieOptions);

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new UserResponse(user));
    }

    public UserDetails getLoggedUser(){
        // Logged user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Getting logged user...");
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        }
        log.info("User not authenticated.");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}
