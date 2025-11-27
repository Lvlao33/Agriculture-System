package com.farmporject.backend.trade.repository;

import com.farmporject.backend.trade.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByIsDefaultDescCreateTimeDesc(Long userId);
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
    void deleteByUserIdAndId(Long userId, Long id);
}