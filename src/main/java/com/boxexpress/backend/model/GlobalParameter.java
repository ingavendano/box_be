package com.boxexpress.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracion_parametros")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String paramKey; // Renamed from 'key' to avoid SQL keywords

    @Column(nullable = false)
    private String paramValue; // Renamed from 'value'

    private String description;

    @Column(nullable = false)
    private String category; // TAXES, COST_LB, ADMIN_FEES

    @Column(nullable = false)
    private String type; // NUMBER, TEXT, BOOLEAN
}
