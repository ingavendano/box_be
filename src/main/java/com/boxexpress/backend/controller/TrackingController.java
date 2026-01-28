package com.boxexpress.backend.controller;

import com.boxexpress.backend.dto.PublicPackageDTO;
import com.boxexpress.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/public/{trackingId}")
    public ResponseEntity<?> trackPublic(@PathVariable String trackingId) {
        try {
            PublicPackageDTO data = trackingService.trackPackage(trackingId);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
