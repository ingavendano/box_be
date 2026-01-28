package com.boxexpress.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_parametros_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User modifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_id", nullable = false)
    private GlobalParameter parameter;

    private String oldValue;
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime changeDate;

    @PrePersist
    protected void onCreate() {
        changeDate = LocalDateTime.now();
    }
}
