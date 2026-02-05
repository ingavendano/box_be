package com.boxexpress.backend.service;

import com.boxexpress.backend.dto.AuthResponse;
import com.boxexpress.backend.dto.LoginRequest;
import com.boxexpress.backend.dto.RegisterRequest;
import com.boxexpress.backend.model.Role;
import com.boxexpress.backend.model.User;
import com.boxexpress.backend.repository.UserRepository;
import com.boxexpress.backend.security.JwtUtils;
import com.boxexpress.backend.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldCreateUser_WhenEmailValid() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setPhone("12345678");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        // Mocking save to return a user with ID (simulating DB save)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            if (u.getId() == null) {
                u.setId(1L); // Simulate ID generation on first save
            }
            return u;
        });

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("BOXSV0001", response.getLockerNumber()); // Verify locker number logic
        verify(userRepository, times(2)).save(any(User.class)); // 1st save, 2nd save for locker
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = User.builder()
                .email(request.getEmail())
                .fullName("Test User")
                .role(Role.ROLE_CLIENTE)
                .lockerNumber("BOXSV0001")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}
