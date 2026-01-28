package com.boxexpress.backend.repository;

import com.boxexpress.backend.model.TrackingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingStatusRepository extends JpaRepository<TrackingStatus, String> {
    List<TrackingStatus> findAllByOrderBySortOrderAsc();

    List<TrackingStatus> findByActiveTrueOrderBySortOrderAsc();
}
