package com.boxexpress.backend.dto.quote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {
    private Double baseCost;
    private Double tariffCost;
    private Double adminFees;
    private Double homeDeliveryFee;
    private Double taxes;
    private Double totalCost;
}
