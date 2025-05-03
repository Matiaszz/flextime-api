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
import dev.matias.flextime.api.services.UserService;
import dev.matias.flextime.api.utils.ObjectBuilder;
import jakarta.validation.Valid;
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
    private AppointmentService appointmentService;

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
        List<AppointmentResponse> companyAppointments = appointmentRepository.findByCompany_Name(companyName).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found."))
                .stream().map(AppointmentResponse::new).toList();

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
        Company referencedCompany = companyRepository.findByName(companyName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found"));

        List<AppointmentResponse> companyAppointments = appointmentRepository.findByCompany_Name(companyName).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company name not found."))
                .stream().map(AppointmentResponse::new).toList();

        Appointment appointment = objectBuilder.appointmentFromDTO(dto);
        appointment.setCompany(referencedCompany);

        if(!appointmentService.hasOverlap(appointment, companyAppointments)){
            appointmentRepository.save(appointment);
            return ResponseEntity.ok().body(new AppointmentResponse(appointment));
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment overlaps with an existing one.");


    }
}
