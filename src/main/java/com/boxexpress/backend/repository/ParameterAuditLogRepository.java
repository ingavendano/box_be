package com.boxexpress.backend.repository;

import com.boxexpress.backend.model.ParameterAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParameterAuditLogRepository extends JpaRepository<ParameterAuditLog, Long> {
    List<ParameterAuditLog> findTop50ByOrderByChangeDateDesc();
}
