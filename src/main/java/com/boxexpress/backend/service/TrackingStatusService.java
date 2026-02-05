package com.boxexpress.backend.service;

import com.boxexpress.backend.model.TrackingStatus;
import com.boxexpress.backend.repository.TrackingStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingStatusService {

    private final TrackingStatusRepository repository;

    public List<TrackingStatus> getAllStatuses() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public List<TrackingStatus> getActiveStatuses() {
        return repository.findByActiveTrueOrderBySortOrderAsc();
    }

    public TrackingStatus createStatus(TrackingStatus status) {
        if (repository.existsById(status.getCode())) {
            throw new RuntimeException("El código de estado ya existe: " + status.getCode());
        }
        return repository.save(status);
    }

    public TrackingStatus updateStatus(String code, TrackingStatus statusDetails) {
        TrackingStatus status = repository.findById(code)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + code));

        status.setName(statusDetails.getName());
        status.setDescription(statusDetails.getDescription());
        status.setSortOrder(statusDetails.getSortOrder());
        status.setColor(statusDetails.getColor());
        status.setActive(statusDetails.isActive());

        return repository.save(status);
    }

    public void deleteStatus(String code) {
        // Soft delete ideally, or ensure not in use. For now, we'll allow delete if
        // referential integrity allows it,
        // but typically we should check usage.
        // Since we are just starting, we can just delete.
        // Better approach: set active = false
        TrackingStatus status = repository.findById(code)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + code));
        repository.delete(status);
    }

    @Transactional
    public void seedInitialStatuses() {
        if (repository.count() == 0) {
            List<TrackingStatus> initialStatuses = Arrays.asList(
                    TrackingStatus.builder().code("RECEIVED").name("Recibido en bodega")
                            .description("Paquete recibido en nuestras instalaciones").sortOrder(1).active(true)
                            .build(),
                    TrackingStatus.builder().code("IN_TRANSIT").name("En camino")
                            .description("En tránsito (Aéreo/Marítimo)").sortOrder(2).active(true).build(),
                    TrackingStatus.builder().code("IN_CUSTOMS").name("En aduanas")
                            .description("En proceso de liberación aduanal").sortOrder(3).active(true).build(),
                    TrackingStatus.builder().code("READY_PICKUP").name("Listo para retirar")
                            .description("Listo para retirar o en ruta de entrega final").sortOrder(4).active(true)
                            .build(),
                    TrackingStatus.builder().code("DELIVERED").name("Entregado")
                            .description("Paquete entregado al cliente").sortOrder(5).active(true).build(),
                    TrackingStatus.builder().code("CANCELLED").name("Cancelado").description("Envío cancelado")
                            .sortOrder(6).active(true).build());
            repository.saveAll(initialStatuses);
        }
    }
}
