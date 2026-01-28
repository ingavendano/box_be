package com.boxexpress.backend.repository;

import com.boxexpress.backend.model.TariffRange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRangeRepository extends JpaRepository<TariffRange, Long> {
}
