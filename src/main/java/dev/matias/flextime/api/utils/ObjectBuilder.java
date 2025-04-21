package dev.matias.flextime.api.utils;

import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.CompanyRole;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.domain.UserRole;
import dev.matias.flextime.api.dtos.CompanyRegisterDTO;
import dev.matias.flextime.api.dtos.UserRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ObjectBuilder {

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public User userFromDTO(UserRegisterDTO dto){
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

    public Company companyFromDTO(CompanyRegisterDTO dto){
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
}
