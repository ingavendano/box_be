package com.boxexpress.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "packages")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trackingId; // e.g., BOX-123456

    private String description; // e.g., "Caja con ropa"

    // Sensitive info (Not exposed in Public API)
    private String senderName;
    private String receiverName;
    private String receiverPhone;
    private String destinationAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_status", referencedColumnName = "code")
    private TrackingStatus currentStatus;

    private String originCity; // e.g., Houston, TX
    private String destinationCity; // e.g., San Miguel, ES

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private User customer; // Link to registered customer

    // Cost & Dimensions
    private Double weight; // Lbs
    private Double volumetricWeight;
    private String category; // PESO / VOLUMEN
    private String subcategory; // e.g. Ropa, Zapato

    // Financials
    private Double declaredValue; // Valor declarado del paquete
    @Column(name = "tariff_fee")
    private Double tariffFee;
    @Column(name = "trip_id")
    private String tripId; // Cobro por arancel/impuesto específico
    private Double totalCost;
    private String paymentMethod; // CASH, CARD, etc.

    @OneToMany(mappedBy = "packageItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<TrackingEvent> events = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
