package dev.matias.flextime.api.controllers;

import dev.matias.flextime.api.domain.Appointment;
import dev.matias.flextime.api.domain.Company;
import dev.matias.flextime.api.domain.User;
import dev.matias.flextime.api.dtos.AppointmentCreateDTO;
import dev.matias.flextime.api.repositories.AppointmentRepository;
import dev.matias.flextime.api.repositories.CompanyRepository;
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

    @PostMapping("/{companyName}")
    public ResponseEntity<Appointment> createAppointment(@PathVariable String companyName, @RequestBody AppointmentCreateDTO dto){
        Company referencedCompany = companyRepository.findByName(companyName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found"));

        Appointment appointment = objectBuilder.appointmentFromDTO(dto);
        appointment.setCompany(referencedCompany);

        return ResponseEntity.ok().body(appointment);

    }

}
