package com.boxexpress.backend.controller;

import com.boxexpress.backend.model.Travel;
import com.boxexpress.backend.service.TravelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/travels")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;

    @GetMapping("/public")
    public ResponseEntity<List<Travel>> getPublicTravels() {
        return ResponseEntity.ok(travelService.getPublicTravels());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Travel>> getAllTravels() {
        return ResponseEntity.ok(travelService.getAllTravels());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTravel(@RequestBody Travel travel) {
        try {
            return ResponseEntity.ok(travelService.createTravel(travel));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTravel(@PathVariable Long id, @RequestBody Travel travel) {
        try {
            return ResponseEntity.ok(travelService.updateTravel(id, travel));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTravel(@PathVariable Long id) {
        travelService.deleteTravel(id);
        return ResponseEntity.noContent().build();
    }
}
