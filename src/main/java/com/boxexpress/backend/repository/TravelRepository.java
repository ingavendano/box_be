package com.boxexpress.backend.repository;

import com.boxexpress.backend.model.Travel;
import com.boxexpress.backend.model.TravelStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TravelRepository extends JpaRepository<Travel, Long> {

    // Find upcoming or active travels (not cancelled)
    List<Travel> findByStatusNotAndDepartureDateGreaterThanEqual(TravelStatus status, LocalDate date, Sort sort);

    // Simple finder for all non-cancelled for public view
    List<Travel> findByStatusNot(TravelStatus status, Sort sort);
}
