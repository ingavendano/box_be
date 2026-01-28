package com.boxexpress.backend.controller;

import com.boxexpress.backend.dto.AddressDTO;
import com.boxexpress.backend.dto.RegisterRequest;
import com.boxexpress.backend.model.Address;
import com.boxexpress.backend.model.User;
import com.boxexpress.backend.service.CustomerAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CustomerAdminController {

    private final CustomerAdminService customerService;

    @GetMapping
    public ResponseEntity<List<User>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchCustomers(@RequestParam String query) {
        return ResponseEntity.ok(customerService.searchCustomers(query));
    }

    @PostMapping
    public ResponseEntity<User> createCustomer(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<Address>> getAddresses(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerAddresses(id));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<Address> addAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(customerService.addAddress(id, addressDTO));
    }
}
