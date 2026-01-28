package com.boxexpress.backend.dto;

import lombok.Data;

@Data
public class CreatePackageRequest {
    private Long customerId;
    private String description;

    // Address Details (Optional override or ID)
    private Long destinationAddressId; // Use saved address
    private String destinationAddressText; // Or one-time text
    private String senderName; // If different from customer

    // Logistics
    private Double weight;
    private Double volumetricWeight;
    private String category; // PESO / VOLUMEN
    private String subcategory;

    private Double declaredValue;
    private Double totalCost;
}
