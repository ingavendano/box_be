package com.boxexpress.backend.controller;

import com.boxexpress.backend.dto.AddressDTO;
import com.boxexpress.backend.model.Address;
import com.boxexpress.backend.service.CustomerAdminService;
import com.boxexpress.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENTE')")
public class CustomerController {

    private final CustomerAdminService customerService;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<Address>> getMyAddresses() {
        return ResponseEntity.ok(customerService.getCustomerAddresses(getCurrentUserId()));
    }

    @PostMapping("/addresses")
    public ResponseEntity<Address> addMyAddress(@RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(customerService.addAddress(getCurrentUserId(), addressDTO));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody com.boxexpress.backend.dto.UpdateProfileRequest request) {
        return ResponseEntity.ok(customerService.updateProfile(getCurrentUserId(), request));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(customerService.updateAddress(getCurrentUserId(), id, addressDTO));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        customerService.deleteAddress(getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
