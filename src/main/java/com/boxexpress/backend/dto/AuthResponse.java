package com.boxexpress.backend.dto;

import com.boxexpress.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String fullName;
    private String email;
    private Role role;
    private String lockerNumber;
}
