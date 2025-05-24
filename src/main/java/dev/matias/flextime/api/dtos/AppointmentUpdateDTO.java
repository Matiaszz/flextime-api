package dev.matias.flextime.api.dtos;

import dev.matias.flextime.api.domain.Appointment;

import java.time.LocalDateTime;

public record AppointmentUpdateDTO(String name, String description, LocalDateTime startTime, LocalDateTime endTime,
        Boolean confirmed) {

    public AppointmentUpdateDTO(Appointment appointment) {
        this(
                appointment.getName(),
                appointment.getDescription(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.isConfirmed());
    }

}