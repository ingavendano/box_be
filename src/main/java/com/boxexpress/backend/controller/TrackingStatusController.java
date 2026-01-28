package com.boxexpress.backend.controller;

import com.boxexpress.backend.model.TrackingStatus;
import com.boxexpress.backend.service.TrackingStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking-statuses")
@RequiredArgsConstructor
public class TrackingStatusController {

    private final TrackingStatusService service;

    @GetMapping
    public ResponseEntity<List<TrackingStatus>> getAllStatuses() {
        return ResponseEntity.ok(service.getAllStatuses());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TrackingStatus>> getActiveStatuses() {
        return ResponseEntity.ok(service.getActiveStatuses());
    }

    @PostMapping
    public ResponseEntity<TrackingStatus> createStatus(@RequestBody TrackingStatus status) {
        return ResponseEntity.ok(service.createStatus(status));
    }

    @PutMapping("/{code}")
    public ResponseEntity<TrackingStatus> updateStatus(@PathVariable String code, @RequestBody TrackingStatus status) {
        return ResponseEntity.ok(service.updateStatus(code, status));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteStatus(@PathVariable String code) {
        service.deleteStatus(code);
        return ResponseEntity.noContent().build();
    }
}
