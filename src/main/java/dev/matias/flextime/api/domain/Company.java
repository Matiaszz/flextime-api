package dev.matias.flextime.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> workers = new ArrayList<>();

    @Column(nullable = false)
    private CompanyRole role = CompanyRole.COMPANY;

    private boolean enabled = true;

    @Lob
    private String description = "";


    public void addWorker(User user){
        user.setRole(UserRole.WORKER);
        user.setCompany(this);
        workers.add(user);
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
