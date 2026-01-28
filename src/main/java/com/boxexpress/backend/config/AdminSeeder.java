package com.boxexpress.backend.config;

import com.boxexpress.backend.model.Role;
import com.boxexpress.backend.model.User;
import com.boxexpress.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            String email = "carlosavendanoing@gmail.com";
            if (userRepository.findByEmail(email).isEmpty()) {
                User admin = User.builder()
                        .fullName("Carlos Avendano")
                        .email(email)
                        .password(passwordEncoder.encode("password123")) // Default password
                        .phone("+503 0000-0000")
                        .role(Role.ROLE_ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("Admin User Created: " + email);
            } else {
                // Ensure role is ADMIN
                User existing = userRepository.findByEmail(email).get();
                if (existing.getRole() != Role.ROLE_ADMIN) {
                    existing.setRole(Role.ROLE_ADMIN);
                    userRepository.save(existing);
                    System.out.println("User Role updated to ROLE_ADMIN for: " + email);
                }
            }
        };
    }
}
