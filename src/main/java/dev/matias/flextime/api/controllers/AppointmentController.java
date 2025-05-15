package dev.matias.flextime.api.controllers;

import dev.matias.flextime.api.domain.Appointment;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.dtos.AppointmentCreateDTO;
import dev.matias.flextime.api.dtos.AppointmentUpdateDTO;
import dev.matias.flextime.api.repositories.AppointmentRepository;
import dev.matias.flextime.api.repositories.CompanyRepository;
import dev.matias.flextime.api.responses.AppointmentResponse;
import dev.matias.flextime.api.services.AppointmentService;
import dev.matias.flextime.api.services.CompanyService;
import dev.matias.flextime.api.services.TokenService;
import dev.matias.flextime.api.services.UserService;
import dev.matias.flextime.api.utils.ObjectBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final TokenService tokenService;
    private final AppointmentService appointmentService;

    @GetMapping("/company/{companyName}/confirmed/")
    public ResponseEntity<List<AppointmentResponse>> getConfirmedAppointmentsByCompany(@PathVariable String companyName){
        List<AppointmentResponse> companyAppointments = appointmentService.getConfirmedCompanyAppointments(companyName);

        if (companyAppointments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(companyAppointments);
    }

    @GetMapping("/company/{companyName}/")
    public ResponseEntity<List<AppointmentResponse>> getCompanyAppointments(@PathVariable String companyName){
        List<AppointmentResponse> companyAppointments = appointmentService.getAppointmentsByCompany(companyName)
                .stream()
                .map(AppointmentResponse::new)
                .toList();

        return ResponseEntity.ok(companyAppointments);
    }

    @GetMapping("/appointment/{appointmentSlug}/")
    public ResponseEntity<AppointmentResponse> getAppointmentBySlug(@PathVariable String appointmentSlug){

        Appointment appointment = appointmentService.getAppointmentBySlug(appointmentSlug);

        return ResponseEntity.ok(new AppointmentResponse(appointment));
    }

    @PatchMapping("/appointment/{companyName}/{appointmentSlug}/")
    public ResponseEntity<AppointmentResponse> patchAppointmentBySlug(@PathVariable String companyName, @PathVariable String appointmentSlug, @RequestBody @Valid AppointmentUpdateDTO appointmentUpdateDTO){
        Appointment appointment = appointmentService.updateAppointment(appointmentUpdateDTO, companyName, appointmentSlug);

        return ResponseEntity.ok(new AppointmentResponse(appointment));
    }



    @PostMapping("/{companyName}/")
    public ResponseEntity<AppointmentResponse> createAppointment(@PathVariable String companyName, @RequestBody @Valid AppointmentCreateDTO dto){
        return ResponseEntity.ok(appointmentService.createAppointment(companyName, dto));
    }

    @DeleteMapping("/{companyName}/{appointmentSlug}/")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String companyName, @PathVariable String appointmentSlug){
        List<Appointment> companyAppointments = appointmentService.getAppointmentsByCompany(companyName);
        Appointment appointment = appointmentService.getAppointmentBySlug(appointmentSlug);
        Object loggedEntity = tokenService.getLoggedEntity();

        if (loggedEntity == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User/Company not authenticated");
        }

        if (loggedEntity instanceof User user){
            if (user.getCompany() == null){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is a client, cannot delete any appointment");
            }

            if (!user.getCompany().getName().equals(companyName)){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not a worker from " + companyName);
            }
            appointmentService.deleteAppointment(appointment);
            return ResponseEntity.ok().build();
        }
        Company company = (Company) loggedEntity;

        if (!company.getName().equals(companyName)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This company is not owner of the appointment.");
        }

        if (!companyAppointments.contains(appointment)){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Appointment " + appointment.getSlug() + " not in " + companyName + " appointments");
        }

        appointmentService.deleteAppointment(appointment);
        return ResponseEntity.ok().build();
    }
}
