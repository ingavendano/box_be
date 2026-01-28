package com.boxexpress.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String type; // USA, ESA

    // Common fields
    private String contactName;
    private String phone;
    private String instructions; // Reference point or delivery instructions

    // USA Specific
    private String street;
    private String city; // Default Houston
    private String state; // Default TX
    private String zipCode;

    // ESA Specific
    private String department; // San Miguel, etc.
    private String municipality;
    private String referencePoint; // Can map to instructions too, but keeping distinct if needed
}
