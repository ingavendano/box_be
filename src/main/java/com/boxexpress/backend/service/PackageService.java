package com.boxexpress.backend.service;

import com.boxexpress.backend.dto.CreatePackageRequest;
import com.boxexpress.backend.dto.BulkStatusUpdateRequest;
import com.boxexpress.backend.model.*;
import com.boxexpress.backend.model.Package;
import com.boxexpress.backend.repository.AddressRepository;
import com.boxexpress.backend.repository.PackageRepository;
import com.boxexpress.backend.repository.TrackingStatusRepository;
import com.boxexpress.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final TrackingStatusRepository trackingStatusRepository;
    private final EmailService emailService;

    @Transactional
    public Package createPackage(CreatePackageRequest request) {
        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        }

        String trackingId = generateTrackingId();

        // Resolve Address
        String destAddress = request.getDestinationAddressText();
        if (request.getDestinationAddressId() != null) {
            Address addr = addressRepository.findById(request.getDestinationAddressId())
                    .orElse(null);
            if (addr != null) {
                // Format: City, Department. Ref: ...
                destAddress = String.format("%s, %s. Ref: %s",
                        addr.getMunicipality(), addr.getDepartment(), addr.getReferencePoint());
            }
        }

        // Resolve default status
        TrackingStatus receivedStatus = trackingStatusRepository.findById("RECEIVED")
                .orElseThrow(() -> new RuntimeException("Estado 'RECEIVED' no encontrado en base de datos"));

        Package pkg = Package.builder()
                .trackingId(trackingId)
                .description(request.getDescription())
                .customer(customer)
                .senderName(request.getSenderName() != null ? request.getSenderName() : "Box Express Office")
                .receiverName(customer != null ? customer.getFullName() : "N/A")
                .destinationAddress(destAddress)
                .currentStatus(receivedStatus)
                .originCity("Houston, TX")
                .destinationCity("El Salvador")
                .weight(request.getWeight())
                .volumetricWeight(request.getVolumetricWeight())
                .category(request.getCategory())
                .subcategory(request.getSubcategory())
                .declaredValue(request.getDeclaredValue())
                .tariffFee(0.0)
                .totalCost(request.getTotalCost())
                .build();

        // Add initial event
        TrackingEvent event = TrackingEvent.builder()
                .packageItem(pkg)
                .status(receivedStatus)
                .location("Houston Warehouse")
                .description("Paquete recibido en oficina")
                .timestamp(LocalDateTime.now())
                .build();

        pkg.getEvents().add(event);

        Package savedPkg = packageRepository.save(pkg);

        // Send Email Notification
        if (customer != null && customer.getEmail() != null) {
            emailService.sendTrackingUpdate(
                    customer.getEmail(),
                    customer.getFullName(),
                    savedPkg.getTrackingId(),
                    receivedStatus.getName().toUpperCase(),
                    "http://localhost:4200/tracking?id=" + savedPkg.getTrackingId());
        }

        return savedPkg;
    }

    public List<Package> getPendingPackages(Long customerId) {
        TrackingStatus deliveredStatus = trackingStatusRepository.findById("DELIVERED")
                .orElse(null); // If not found, we might want to return all or handle error.
        // However, logic is 'not delivered'. If delivered status doesn't exist,
        // technically nothing is delivered.

        if (deliveredStatus == null) {
            return packageRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId);
        }

        return packageRepository.findByCustomer_IdAndCurrentStatusNot(customerId, deliveredStatus);
    }

    @Transactional
    public List<Package> bulkUpdateStatus(BulkStatusUpdateRequest request) {
        List<Package> packages = packageRepository.findAllById(request.getPackageIds());

        TrackingStatus newStatus = null;
        if (request.getStatusCode() != null) {
            newStatus = trackingStatusRepository.findById(request.getStatusCode())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + request.getStatusCode()));
        }

        // Final effective status for the loop
        TrackingStatus statusToApply = newStatus;

        packages.forEach(pkg -> {
            if (statusToApply != null) {
                pkg.setCurrentStatus(statusToApply);
            }

            TrackingEvent event = TrackingEvent.builder()
                    .packageItem(pkg)
                    .status(pkg.getCurrentStatus()) // Use current status
                    .location(request.getLocation() != null ? request.getLocation() : "Actualización Masiva")
                    .description(request.getDescription())
                    .timestamp(LocalDateTime.now())
                    .build();

            pkg.getEvents().add(event);

            // Send Email Notification
            if (pkg.getCustomer() != null && pkg.getCustomer().getEmail() != null) {
                emailService.sendTrackingUpdate(
                        pkg.getCustomer().getEmail(),
                        pkg.getCustomer().getFullName(),
                        pkg.getTrackingId(),
                        pkg.getCurrentStatus().getName().toUpperCase(),
                        "http://localhost:4200/tracking?id=" + pkg.getTrackingId());
            }
        });

        return packageRepository.saveAll(packages);
    }

    @Transactional(readOnly = true)
    public List<Package> searchPackages(String tripId, String statusCode) {
        TrackingStatus status = null;
        if (statusCode != null && !statusCode.isEmpty()) {
            status = trackingStatusRepository.findById(statusCode).orElse(null);
        }
        return packageRepository.searchPackages(tripId, status);
    }

    public Package getPackageById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado con ID: " + id));
    }

    public List<Package> getPackagesByUserId(Long userId) {
        return packageRepository.findByCustomer_IdOrderByCreatedAtDesc(userId);
    }

    public List<Package> getPackagesByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return getPackagesByUserId(user.getId());
    }

    private synchronized String generateTrackingId() {
        int year = LocalDateTime.now().getYear();
        String prefix = String.format("BOX-%d-", year);

        // Find the last ID with this prefix
        return packageRepository.findTopByTrackingIdStartingWithOrderByTrackingIdDesc(prefix)
                .map(pkg -> {
                    String currentId = pkg.getTrackingId(); // e.g., BOX-2026-000005
                    try {
                        String[] parts = currentId.split("-");
                        if (parts.length == 3) {
                            int sequence = Integer.parseInt(parts[2]);
                            return String.format("%s%06d", prefix, sequence + 1);
                        }
                    } catch (NumberFormatException e) {
                        // If format is invalid, fall through
                    }
                    return prefix + "000001"; // Fallback
                })
                .orElse(prefix + "000001"); // First package of the year
    }
}
