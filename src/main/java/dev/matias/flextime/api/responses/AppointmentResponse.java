package dev.matias.flextime.api.responses;

import dev.matias.flextime.api.domain.Appointment;

import java.time.LocalDateTime;

public record AppointmentResponse(
        String id,
        String name,
        String slug,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean confirmed,
        ClientInfo client,
        CompanyInfo company
) {
    public AppointmentResponse(Appointment appointment) {
        this(
                appointment.getId().toString(),
                appointment.getName(),
                appointment.getSlug(),
                appointment.getDescription(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.isConfirmed(),
                new ClientInfo(
                        appointment.getClient().getId().toString(),
                        appointment.getClient().getName(),
                        appointment.getClient().getLastName(),
                        appointment.getClient().getEmail()
                ),
                new CompanyInfo(
                        appointment.getCompany().getId().toString(),
                        appointment.getCompany().getName(),
                        appointment.getCompany().getEmail()
                )
        );
    }

    public record ClientInfo(
            String id,
            String name,
            String lastName,
            String email
    ) {}

    public record CompanyInfo(
            String id,
            String name,
            String email
    ) {}
}

