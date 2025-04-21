package dev.matias.flextime.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.matias.flextime.api.repositories.CompanyRepository;
import dev.matias.flextime.api.repositories.UserRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Company implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String username;

    @Size(min = 6)
    @JsonIgnore
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<User> workers = new ArrayList<>();

    @Column(nullable = false)
    private CompanyRole role = CompanyRole.COMPANY;

    private boolean enabled = true;

    @Lob
    private String description = "";


    @Transactional
    public void addWorker(User user, UserRepository userRepository, CompanyRepository companyRepository){
        if (user.getRole().equals(UserRole.WORKER) && user.getCompany() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This user already is a worker of another company. Please, ensure the user is a client-level before their assignment.");
        }

        if(!user.isEnabled()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not enabled");
        }

        if(!workers.contains(user)){
            user.setRole(UserRole.WORKER);
            user.setCompany(this);
            workers.add(user);

            userRepository.save(user);
            companyRepository.save(this);
        }

    }

    // Implementation methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return this.role.getPermissions().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
