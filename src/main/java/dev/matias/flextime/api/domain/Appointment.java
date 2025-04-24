package dev.matias.flextime.api.domain;
import dev.matias.flextime.api.validations.Slug;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne
    private User client;

    @ManyToOne
    private Company company;

    @Column(unique = true)
    @Slug
    private String slug;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean confirmed;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
