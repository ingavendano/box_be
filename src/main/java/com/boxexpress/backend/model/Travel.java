package com.boxexpress.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "travels")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Travel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDate closingDate;

    @Column(nullable = false)
    private LocalDate departureDate;

    @Column(nullable = false)
    private LocalDate arrivalDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TravelStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType transportType;
}
