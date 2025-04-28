package dev.matias.flextime.api.controllers;

import dev.matias.flextime.api.domain.Appointment;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.dtos.AppointmentCreateDTO;
import dev.matias.flextime.api.repositories.AppointmentRepository;
import dev.matias.flextime.api.repositories.CompanyRepository;
import dev.matias.flextime.api.responses.AppointmentResponse;
import dev.matias.flextime.api.services.AppointmentService;
import dev.matias.flextime.api.services.UserService;
import dev.matias.flextime.api.utils.ObjectBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ObjectBuilder objectBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/{companyName}")
    public ResponseEntity<AppointmentResponse> createAppointment(@PathVariable String companyName, @RequestBody AppointmentCreateDTO dto){
        Company referencedCompany = companyRepository.findByName(companyName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found"));

        List<AppointmentResponse> companyAppointments = appointmentRepository.findByCompany_Name(companyName).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found."))
                .stream().map(AppointmentResponse::new).toList();

        Appointment appointment = objectBuilder.appointmentFromDTO(dto);
        appointment.setCompany(referencedCompany);

        appointmentService.createAppointment(appointment, companyAppointments);

        return ResponseEntity.ok().body(new AppointmentResponse(appointment));

    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<List<AppointmentResponse>> getCompanyAppointments(@PathVariable String companyName){
        List<AppointmentResponse> companyAppointments = appointmentRepository.findByCompany_Name(companyName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found."))
                .stream().map(AppointmentResponse::new).toList();

        return ResponseEntity.ok(companyAppointments);
    }

    @GetMapping("/appointment/{appointmentSlug}")
    public ResponseEntity<AppointmentResponse> getAppointmentBySlug(@PathVariable String appointmentSlug){
        Appointment appointment= appointmentRepository.findBySlug(appointmentSlug).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment slug not found."));

        return ResponseEntity.ok(new AppointmentResponse(appointment));
    }

    @GetMapping("/company/{companyName}/confirmed")
    public ResponseEntity<List<AppointmentResponse>> getConfirmedAppointmentsByCompany(@PathVariable String companyName){
        List<AppointmentResponse> companyAppointments = appointmentRepository.findByCompany_Name(companyName).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found."))
                .stream()
                .filter(Appointment::isConfirmed)
                .map(AppointmentResponse::new).toList();

        if (companyAppointments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(companyAppointments);
    }
}
