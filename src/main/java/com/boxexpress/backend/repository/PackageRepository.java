package com.boxexpress.backend.repository;

import com.boxexpress.backend.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import com.boxexpress.backend.model.TrackingStatus;

public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByTrackingId(String trackingId);

    List<Package> findByCustomer_IdAndCurrentStatusNot(Long customerId, TrackingStatus status);

    @Query("SELECT p FROM Package p WHERE (:tripId IS NULL OR p.tripId = :tripId) AND (:status IS NULL OR p.currentStatus = :status)")
    List<Package> searchPackages(@Param("tripId") String tripId, @Param("status") TrackingStatus status);

    List<Package> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);

    Optional<Package> findTopByTrackingIdStartingWithOrderByTrackingIdDesc(String prefix);
}
