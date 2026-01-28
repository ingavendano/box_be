package com.boxexpress.backend.config;

import com.boxexpress.backend.service.TrackingStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final TrackingStatusService trackingStatusService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            trackingStatusService.seedInitialStatuses();
        };
    }
}
