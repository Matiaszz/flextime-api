package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.domain.Appointment;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.validations.Slug;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;


public record AppointmentCreateDTO(@NotNull String name, String description,@NotNull LocalDateTime startTime, @NotNull LocalDateTime endTime, Boolean confirmed) {

    public AppointmentCreateDTO(Appointment appointment){
        this(
                appointment.getName(),
                appointment.getDescription(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.isConfirmed()
        );
    }

}
