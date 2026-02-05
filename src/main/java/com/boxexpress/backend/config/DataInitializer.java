package com.boxexpress.backend.config;

import com.boxexpress.backend.model.Role;
import com.boxexpress.backend.model.User;
import com.boxexpress.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final com.boxexpress.backend.repository.TrackingStatusRepository trackingStatusRepository;

    @Override
    public void run(String... args) throws Exception {
        migrateUsers();
        migrateStatusColors();
    }

    private void migrateUsers() {
        log.info("Checking for users without locker number...");
        // This is a simple migration. For production with thousands of users, do this
        // in SQL or batch.
        List<User> users = userRepository.findAll();

        int count = 0;
        for (User user : users) {
            if (user.getRole() == Role.ROLE_CLIENTE
                    && (user.getLockerNumber() == null || user.getLockerNumber().isEmpty())) {
                String lockerNumber = "BOX-SV-" + String.format("%09d", user.getId());
                user.setLockerNumber(lockerNumber);
                userRepository.save(user);
                count++;
            }
        }

        if (count > 0) {
            log.info("Migrated {} users with new Locker Numbers.", count);
        }
    }

    private void migrateStatusColors() {
        log.info("Checking for statuses without color...");
        List<com.boxexpress.backend.model.TrackingStatus> statuses = trackingStatusRepository.findAll();
        int count = 0;
        for (com.boxexpress.backend.model.TrackingStatus status : statuses) {
            if (status.getColor() == null || status.getColor().isEmpty()) {
                String color = switch (status.getCode()) {
                    case "RECEIVED", "IN_TRANSIT" -> "#3B82F6"; // Blue
                    case "IN_CUSTOMS" -> "#F97316"; // Orange
                    case "READY_PICKUP", "DELIVERED" -> "#22C55E"; // Green
                    case "CANCELLED" -> "#EF4444"; // Red
                    default -> "#6B7280"; // Gray
                };
                status.setColor(color);
                trackingStatusRepository.save(status);
                count++;
            }
        }
        if (count > 0) {
            log.info("Migrated {} tracking statuses with default colors.", count);
        }
    }
}
