package dev.matias.flextime.api.services;

import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.dtos.UserRegisterDTO;
import dev.matias.flextime.api.repositories.UserRepository;
import dev.matias.flextime.api.utils.ObjectBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectBuilder objectBuilder;

    public User fromDTO(UserRegisterDTO dto) {
        return objectBuilder.userFromDTO(dto);
    }

    public UserDetails getLoggedUser() {
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
