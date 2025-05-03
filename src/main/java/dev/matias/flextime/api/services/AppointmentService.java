package dev.matias.flextime.api.services;

import dev.matias.flextime.api.domain.Appointment;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.dtos.AppointmentCreateDTO;
import dev.matias.flextime.api.dtos.AppointmentUpdateDTO;
import dev.matias.flextime.api.repositories.AppointmentRepository;
import dev.matias.flextime.api.repositories.CompanyRepository;
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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

    public List<AppointmentResponse> getConfirmedCompanyAppointments(String companyName){

        return appointmentRepository.findByCompany_Name(companyName).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found."))
                .stream()
                .filter(Appointment::isConfirmed)
                .map(AppointmentResponse::new).toList();
    }

    public Appointment getAppointmentBySlug(String slug){
        return appointmentRepository.findBySlug(slug).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment slug not found."));
    }

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

    public Appointment updateAppointment(AppointmentUpdateDTO appointmentUpdateDTO, String companyName, String appointmentSlug){
        Appointment appointment = appointmentRepository.findBySlug(appointmentSlug).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment slug not found."));

        Company company = companyRepository.findByName(companyName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found"));

        List<AppointmentResponse> companyAppointments = appointmentRepository.findByCompany_Name(companyName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found."))
                .stream()
                .filter(app -> app != appointment)
                .map(AppointmentResponse::new).toList();

        User user = (User) userService.getLoggedUser();

        if (!(appointment.getClient().equals(user) || company.getWorkers().contains(user))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized to edit this appointment.");
        }

        if (appointmentUpdateDTO.name() != null){
            appointment.setName(appointmentUpdateDTO.name());
            appointment.setSlug(appointmentUpdateDTO.name().toLowerCase().replace(" ", "-"));
        }

        if (appointmentUpdateDTO.description() != null){
            appointment.setDescription(appointmentUpdateDTO.description());
        }

        if (appointmentUpdateDTO.startTime() != null) {
            appointment.setStartTime(appointmentUpdateDTO.startTime());
        }
        if (appointmentUpdateDTO.endTime() != null) {
            appointment.setEndTime(appointmentUpdateDTO.endTime());
        }
        boolean overlapped = hasOverlap(appointment, companyAppointments);
        if (overlapped){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment overlaps with an existing one.");
        }
        if (appointmentUpdateDTO.confirmed() != null) {
            appointment.setConfirmed(appointmentUpdateDTO.confirmed());
        }

        return appointmentRepository.save(appointment);
    }
}
