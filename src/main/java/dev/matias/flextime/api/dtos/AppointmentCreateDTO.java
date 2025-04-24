package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.domain.Appointment;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.validations.Slug;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentCreateDTO(UUID id, String name, User client, Company company, @Slug String slug, String description, LocalDateTime startTime, LocalDateTime endTime, boolean confirmed) {

    public AppointmentCreateDTO(Appointment appointment){
        this(
                appointment.getId(),
                appointment.getName(),
                appointment.getClient(),
                appointment.getCompany(),
                appointment.getSlug(),
                appointment.getDescription(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.isConfirmed()
        );
    }
}
