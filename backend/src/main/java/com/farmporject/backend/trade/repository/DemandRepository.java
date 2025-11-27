package com.farmporject.backend.trade.repository;

import com.farmporject.backend.trade.model.Demand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {
    List<Demand> findByUserIdOrderByCreateTimeDesc(Long userId);
    List<Demand> findByTitleContainingIgnoreCase(String title);
    List<Demand> findByCategory(String category);
    List<Demand> findByStatusOrderByCreateTimeDesc(String status);

    @Query("SELECT d FROM Demand d WHERE d.title LIKE %:keyword% OR d.description LIKE %:keyword%")
    List<Demand> findByKeyword(@Param("keyword") String keyword);
}