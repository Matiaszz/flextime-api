package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.domain.Appointment;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentCreateDTO(@NotNull String name, String description, @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime, Boolean confirmed) {

    public AppointmentCreateDTO(Appointment appointment) {
        this(
                appointment.getName(),
                appointment.getDescription(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.isConfirmed());
    }

}
