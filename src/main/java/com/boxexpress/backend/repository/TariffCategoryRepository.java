package com.boxexpress.backend.repository;

import com.boxexpress.backend.model.TariffCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TariffCategoryRepository extends JpaRepository<TariffCategory, Long> {
    Optional<TariffCategory> findByName(String name);
}
