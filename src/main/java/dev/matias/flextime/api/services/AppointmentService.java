package dev.matias.flextime.api.services;

import dev.matias.flextime.api.domain.Appointment;
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

    public void createAppointment(Appointment appointment, List<AppointmentResponse> companyAppointments){

        LocalDateTime appointmentStartTime = appointment.getStartTime();
        LocalDateTime appointmentEndTime = appointment.getEndTime();



        if (appointmentStartTime.isAfter(appointmentEndTime)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Start time can't be after than end time.");
        }

        boolean hasOverlap = companyAppointments.stream().anyMatch(existing -> appointmentStartTime.isBefore(existing.endTime()) &&
                appointmentEndTime.isAfter(existing.startTime())
        );

        if (hasOverlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment overlaps with an existing one.");
        }

        appointmentRepository.save(appointment);
    }
}
