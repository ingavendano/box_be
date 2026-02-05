package com.boxexpress.backend.service;

import com.boxexpress.backend.dto.AuthResponse;
import com.boxexpress.backend.dto.LoginRequest;
import com.boxexpress.backend.dto.RegisterRequest;
import com.boxexpress.backend.model.Role;
import com.boxexpress.backend.model.User;
import com.boxexpress.backend.repository.UserRepository;
import com.boxexpress.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;
        private final com.boxexpress.backend.security.UserDetailsServiceImpl userDetailsService;

        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("Email already exists");
                }

                var user = User.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .phone(request.getPhone())
                                .role(request.getRole() != null ? request.getRole() : Role.ROLE_CLIENTE)
                                .build();

                userRepository.save(user);

                // Generate Locker Number
                if (user.getRole() == Role.ROLE_CLIENTE) {
                        String lockerNumber = "BOXSV" + String.format("%04d", user.getId());
                        user.setLockerNumber(lockerNumber);
                        userRepository.save(user);
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                String jwtToken = jwtUtils.generateToken(userDetails);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .lockerNumber(user.getLockerNumber())
                                .build();
        }

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                var jwtToken = jwtUtils.generateToken(userDetails);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .lockerNumber(user.getLockerNumber())
                                .build();
        }
}
