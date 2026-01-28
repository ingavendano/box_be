package com.boxexpress.backend.service;

import com.boxexpress.backend.dto.PublicPackageDTO;
import com.boxexpress.backend.model.Package;
import com.boxexpress.backend.model.TrackingEvent;
import com.boxexpress.backend.model.TrackingStatus;
import com.boxexpress.backend.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingService {

        private final PackageRepository packageRepository;

        @Transactional(readOnly = true)
        public PublicPackageDTO trackPackage(String trackingId) {
                Package pkg = packageRepository.findByTrackingId(trackingId)
                                .orElseThrow(() -> new RuntimeException("Guía no encontrada: " + trackingId));

                List<PublicPackageDTO.PublicEventDTO> history = pkg.getEvents().stream()
                                .sorted(Comparator.comparing(TrackingEvent::getTimestamp).reversed())
                                .map(event -> PublicPackageDTO.PublicEventDTO.builder()
                                                .status(event.getStatus())
                                                .location(event.getLocation())
                                                .description(event.getDescription())
                                                .timestamp(event.getTimestamp())
                                                .build())
                                .collect(Collectors.toList());

                return PublicPackageDTO.builder()
                                .trackingId(pkg.getTrackingId())
                                .description(pkg.getDescription())
                                .currentStatus(pkg.getCurrentStatus())
                                .originCity(pkg.getOriginCity())
                                .destinationCity(pkg.getDestinationCity())
                                .lastUpdate(pkg.getUpdatedAt())
                                .history(history)
                                .build();
        }
}
