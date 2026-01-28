package com.boxexpress.backend.controller;

import com.boxexpress.backend.dto.CreatePackageRequest;
import com.boxexpress.backend.dto.BulkStatusUpdateRequest;
import com.boxexpress.backend.model.Package;
import com.boxexpress.backend.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Package> createPackage(@RequestBody CreatePackageRequest request) {
        return ResponseEntity.ok(packageService.createPackage(request));
    }

    @GetMapping("/customer/{id}/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Package>> getPendingPackages(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.getPendingPackages(id));
    }

    @PutMapping("/bulk-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Package>> bulkUpdateStatus(@RequestBody BulkStatusUpdateRequest request) {
        return ResponseEntity.ok(packageService.bulkUpdateStatus(request));
    }

    @Autowired
    private com.boxexpress.backend.service.LabelService labelService;

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Package>> searchPackages(
            @RequestParam(required = false) String tripId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(packageService.searchPackages(tripId, status));
    }

    @GetMapping("/{id}/label")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> getPackageLabel(@PathVariable Long id) {
        Package pkg = packageService.getPackageById(id);
        byte[] pdfBytes = labelService.generateLabel(pkg);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=label-" + pkg.getTrackingId() + ".pdf")
                .body(pdfBytes);
    }

    @GetMapping("/customer/my-packages")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Package>> getMyPackages() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(packageService.getPackagesByUserEmail(email));
    }
}
