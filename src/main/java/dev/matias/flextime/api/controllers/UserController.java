package dev.matias.flextime.api.controllers;

import dev.matias.flextime.api.config.security.SecurityConfig;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.dtos.UserLoginDTO;
import dev.matias.flextime.api.dtos.UserRegisterDTO;
import dev.matias.flextime.api.repositories.UserRepository;
import dev.matias.flextime.api.responses.UserResponse;
import dev.matias.flextime.api.services.TokenService;
import dev.matias.flextime.api.services.UserService;
import dev.matias.flextime.api.utils.CookieOptions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CookieOptions cookieOptions;


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterDTO registerDTO){
        if (userRepository.findByEmail(registerDTO.email()).isPresent()) {
            log.warn("User {} already exists", registerDTO.username());
            return ResponseEntity.badRequest().build();
        }

        User user = userService.fromDTO(registerDTO);
        userRepository.save(user);

        return userService.generateTokenAndCreateCookie(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginDTO loginDTO) {
        User user = (User) userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isPasswordCorrect = passwordEncoder.matches(loginDTO.password(), user.getPassword());
        log.info("Password match: {}", isPasswordCorrect);

        var usernamePassword = new UsernamePasswordAuthenticationToken(user.getUsername(), loginDTO.password());
        log.info("{}", usernamePassword);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(usernamePassword);
        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", loginDTO.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password " + e);
        }

        var response = userService.generateTokenAndCreateCookie((User) authentication.getPrincipal());

        log.info("Login successful for user: {}", loginDTO.email());

        return response;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUser(HttpServletRequest request) {
        return ResponseEntity.ok(new UserResponse((User) userService.getLoggedUser()));

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie cookie = ResponseCookie.from("userToken", "").httpOnly(true).secure(true)
                .sameSite("None").path("/").maxAge(0).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
