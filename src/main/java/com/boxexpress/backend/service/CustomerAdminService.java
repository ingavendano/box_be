package com.boxexpress.backend.service;

import com.boxexpress.backend.dto.AddressDTO;
import com.boxexpress.backend.dto.AuthResponse;
import com.boxexpress.backend.dto.RegisterRequest;
import com.boxexpress.backend.model.Address;
import com.boxexpress.backend.model.Role;
import com.boxexpress.backend.model.User;
import com.boxexpress.backend.repository.AddressRepository;
import com.boxexpress.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerAdminService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public User createCustomer(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_CLIENTE) // Force Client Role
                .build();

        return userRepository.save(user);
    }

    public Address addAddress(Long userId, AddressDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Address address = Address.builder()
                .user(user)
                .type(dto.getType())
                .contactName(dto.getContactName())
                .phone(dto.getPhone())
                .instructions(dto.getInstructions())
                // USA
                .street(dto.getStreet())
                .city(dto.getCity() != null ? dto.getCity() : "Houston")
                .state(dto.getState() != null ? dto.getState() : "Texas")
                .zipCode(dto.getZipCode())
                // ESA
                .department(dto.getDepartment())
                .municipality(dto.getMunicipality())
                .referencePoint(dto.getReferencePoint())
                .build();

        return addressRepository.save(address);
    }

    public List<User> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_CLIENTE)
                .filter(u -> u.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                        u.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                        (u.getPhone() != null && u.getPhone().contains(query)))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<User> getAllCustomers() {
        // In a real app, this should be paginated and filtered by Role
        // For now, assuming most users are clients or we filter in memory if strictly
        // needed
        // Ideally: userRepository.findByRole(Role.ROLE_CLIENTE);
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_CLIENTE)
                .collect(Collectors.toList());
    }

    public List<Address> getCustomerAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public User updateProfile(Long userId, com.boxexpress.backend.dto.UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    public Address updateAddress(Long userId, Long addressId, AddressDTO dto) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tiene permiso para editar esta dirección");
        }

        address.setContactName(dto.getContactName());
        address.setPhone(dto.getPhone());
        address.setDepartment(dto.getDepartment());
        address.setMunicipality(dto.getMunicipality());
        address.setStreet(dto.getStreet());
        address.setReferencePoint(dto.getReferencePoint());
        // Add other fields as necessary

        return addressRepository.save(address);
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tiene permiso para eliminar esta dirección");
        }

        addressRepository.delete(address);
    }
}
