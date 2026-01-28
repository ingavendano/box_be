package com.boxexpress.backend.repository;

import com.boxexpress.backend.model.GlobalParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalParameterRepository extends JpaRepository<GlobalParameter, Long> {
    Optional<GlobalParameter> findByParamKey(String paramKey);
}
