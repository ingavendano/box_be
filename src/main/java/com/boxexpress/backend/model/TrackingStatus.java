package com.boxexpress.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tracking_statuses")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackingStatus {
    @Id
    private String code; // e.g., "RECEIVED"

    private String name; // e.g., "Recibido en bodega"
    private String description; // Detailed description if needed
    private Integer sortOrder; // To control display order
    private String color; // Hex color code (e.g., #3B82F6)
    private boolean active; // To soft delete or hide statuses
}
