package com.boxexpress.backend.service;

import com.boxexpress.backend.model.GlobalParameter;
import com.boxexpress.backend.model.ParameterAuditLog;
import com.boxexpress.backend.model.User;
import com.boxexpress.backend.repository.GlobalParameterRepository;
import com.boxexpress.backend.repository.ParameterAuditLogRepository;
import com.boxexpress.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GlobalParameterService {

    @Autowired
    private GlobalParameterRepository parameterRepository;

    @Autowired
    private ParameterAuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        if (parameterRepository.count() == 0) {
            createParam("TAX_IVA", "0.13", "Impuesto al Valor Agregado", "TAXES", "NUMBER");
            createParam("TAX_RETENTION", "0.01", "Retención Renta", "TAXES", "NUMBER");
            createParam("COST_LB_LOW", "2.50", "Costo por Libra (<= 10lb)", "COST_LB", "NUMBER");
            createParam("COST_LB_HIGH", "2.00", "Costo por Libra (> 10lb)", "COST_LB", "NUMBER");
            createParam("FEE_AIRPORT", "5.00", "Gestión de Aeropuerto", "ADMIN_FEES", "NUMBER");
            createParam("FEE_ADMIN", "2.50", "Cobro Administrativo Fijo", "ADMIN_FEES", "NUMBER");
        }

        // Ensure Home Delivery Fee exists (added later)
        if (parameterRepository.findByParamKey("FEE_HOME_DELIVERY").isEmpty()) {
            createParam("FEE_HOME_DELIVERY", "3.00", "Servicio a Domicilio", "ADMIN_FEES", "NUMBER");
        }
    }

    private void createParam(String key, String value, String desc, String category, String type) {
        parameterRepository.save(GlobalParameter.builder()
                .paramKey(key)
                .paramValue(value)
                .description(desc)
                .category(category)
                .type(type)
                .build());
    }

    public List<GlobalParameter> getAllParameters() {
        return parameterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<com.boxexpress.backend.dto.ParameterAuditLogDTO> getAuditLogs() {
        return auditLogRepository.findTop50ByOrderByChangeDateDesc().stream()
                .map(log -> com.boxexpress.backend.dto.ParameterAuditLogDTO.builder()
                        .id(log.getId())
                        .oldValue(log.getOldValue())
                        .newValue(log.getNewValue())
                        .changeDate(log.getChangeDate())
                        .modifiedBy(com.boxexpress.backend.dto.ParameterAuditLogDTO.UserSummaryDTO.builder()
                                .username(log.getModifiedBy().getEmail())
                                .fullName(log.getModifiedBy().getFullName())
                                .build())
                        .parameter(com.boxexpress.backend.dto.ParameterAuditLogDTO.ParameterSummaryDTO.builder()
                                .paramKey(log.getParameter().getParamKey())
                                .description(log.getParameter().getDescription())
                                .build())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public GlobalParameter updateParameter(String key, String newValue, String username) {
        GlobalParameter param = parameterRepository.findByParamKey(key)
                .orElseThrow(() -> new RuntimeException("Parámetro no encontrado: " + key));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        String oldValue = param.getParamValue();

        // Update value
        param.setParamValue(newValue);
        GlobalParameter savedParam = parameterRepository.save(param);

        // Create Audit Log
        ParameterAuditLog log = ParameterAuditLog.builder()
                .parameter(savedParam)
                .oldValue(oldValue)
                .newValue(newValue)
                .modifiedBy(user)
                .build();

        auditLogRepository.save(log);

        return savedParam;
    }
}
