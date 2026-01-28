package com.boxexpress.backend.repository;

import com.boxexpress.backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);

    List<Address> findByUserIdAndType(Long userId, String type);
}
