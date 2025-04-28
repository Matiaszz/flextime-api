package dev.matias.flextime.api.services;

import dev.matias.flextime.api.domain.Appointment;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.dtos.AppointmentCreateDTO;
import dev.matias.flextime.api.repositories.AppointmentRepository;
import dev.matias.flextime.api.responses.AppointmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public boolean hasOverlap(Appointment appointment, List<AppointmentResponse> companyAppointments){
        LocalDateTime appointmentStartTime = appointment.getStartTime();
        LocalDateTime appointmentEndTime = appointment.getEndTime();

        boolean overlapped = companyAppointments.stream().anyMatch(existing -> appointmentStartTime.isBefore(existing.endTime()) &&
                appointmentEndTime.isAfter(existing.startTime())
        );
        if (appointmentStartTime.isAfter(appointmentEndTime)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Start time can't be after than end time.");
        }

        return overlapped;
    }

    public void updateAppointment(Appointment appointment, AppointmentCreateDTO appointmentCreateDTO, List<AppointmentResponse> companyAppointments){
        if (appointmentCreateDTO.name() != null){
            appointment.setName(appointmentCreateDTO.name());
            appointment.setSlug(appointmentCreateDTO.name().toLowerCase().replace(" ", "-"));
        }

        if (appointmentCreateDTO.description() != null){
            appointment.setDescription(appointmentCreateDTO.description());
        }

        if (appointmentCreateDTO.startTime() != null) {
            appointment.setStartTime(appointmentCreateDTO.startTime());
        }
        if (appointmentCreateDTO.endTime() != null) {
            appointment.setEndTime(appointmentCreateDTO.endTime());
        }
        boolean overlapped = hasOverlap(appointment, companyAppointments);
        if (overlapped){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment overlaps with an existing one.");
        }
        if (appointmentCreateDTO.confirmed() != null) {
            appointment.setConfirmed(appointmentCreateDTO.confirmed());
        }

        appointmentRepository.save(appointment);
    }
}
