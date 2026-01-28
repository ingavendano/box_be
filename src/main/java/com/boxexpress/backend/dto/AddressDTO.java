package com.boxexpress.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {
    private Long id;
    private String type;
    private String contactName;
    private String phone;
    private String instructions;

    // USA
    private String street;
    private String city;
    private String state;
    private String zipCode;

    // ESA
    private String department;
    private String municipality;
    private String referencePoint;
}
