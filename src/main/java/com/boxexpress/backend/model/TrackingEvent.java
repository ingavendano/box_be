package com.boxexpress.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_events")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    @JsonIgnore
    private Package packageItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status", referencedColumnName = "code")
    private TrackingStatus status;

    private String location; // e.g., "Houston Warehouse"
    private String description; // e.g., "Package received and weighed"

    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
