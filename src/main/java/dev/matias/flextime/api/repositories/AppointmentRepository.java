package dev.matias.flextime.api.repositories;

import dev.matias.flextime.api.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Optional<List<Appointment>> findByCompany_Name(String companyName);

    Optional<Appointment> findBySlug(String slug);

    List<Appointment> findByClient_Username(String username);
}
