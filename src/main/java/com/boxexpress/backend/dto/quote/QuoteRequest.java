package com.boxexpress.backend.dto.quote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequest {
    private Double weight;
    private Double volumetricWeight;
    private Double declaredValue;
    private Long subcategoryId;
    private Boolean isHomeDelivery;
}
