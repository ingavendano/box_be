package com.boxexpress.backend.dto;

import com.boxexpress.backend.model.TrackingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PublicPackageDTO {
    private String trackingId;
    private String description;
    private TrackingStatus currentStatus;
    private String originCity;
    private String destinationCity;
    private LocalDateTime lastUpdate;
    private List<PublicEventDTO> history;

    @Data
    @Builder
    public static class PublicEventDTO {
        private TrackingStatus status;
        private String location;
        private String description;
        private LocalDateTime timestamp;
    }
}
