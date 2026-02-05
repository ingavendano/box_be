package com.boxexpress.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterAuditLogDTO {
    private Long id;
    private String oldValue;
    private String newValue;
    private LocalDateTime changeDate;
    private UserSummaryDTO modifiedBy;
    private ParameterSummaryDTO parameter;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private String username; // Mapping email to this for frontend compatibility
        private String fullName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParameterSummaryDTO {
        private String paramKey;
        private String description;
    }
}
