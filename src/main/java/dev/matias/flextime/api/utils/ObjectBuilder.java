package dev.matias.flextime.api.utils;

import dev.matias.flextime.api.domain.*;
import dev.matias.flextime.api.dtos.AppointmentCreateDTO;
import dev.matias.flextime.api.dtos.CompanyRegisterDTO;
import dev.matias.flextime.api.dtos.UserRegisterDTO;
import dev.matias.flextime.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ObjectBuilder {

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private UserService userService;

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

    public Appointment appointmentFromDTO(AppointmentCreateDTO dto){
        return Appointment.builder()
                .client((User) userService.getLoggedUser())
                .name(dto.name())
                .slug(dto.name().replace(" ", "-"))
                .description(dto.description())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .build();
    }
}
