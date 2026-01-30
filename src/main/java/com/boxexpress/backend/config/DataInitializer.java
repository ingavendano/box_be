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

    @Override
    public void run(String... args) throws Exception {
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
        } else {
            log.info("No users needed migration.");
        }
    }
}
