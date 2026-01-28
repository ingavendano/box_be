package com.boxexpress.backend.controller;

import com.boxexpress.backend.model.GlobalParameter;
import com.boxexpress.backend.model.ParameterAuditLog;
import com.boxexpress.backend.service.GlobalParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class GlobalParameterController {

    @Autowired
    private GlobalParameterService parameterService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<GlobalParameter> getAllSettings() {
        return parameterService.getAllParameters();
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalParameter> updateSetting(
            @PathVariable String key,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {

        String newValue = payload.get("value");
        if (newValue == null) {
            return ResponseEntity.badRequest().build();
        }

        GlobalParameter updated = parameterService.updateParameter(key, newValue, authentication.getName());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ParameterAuditLog> getAuditLogs() {
        return parameterService.getAuditLogs();
    }
}
