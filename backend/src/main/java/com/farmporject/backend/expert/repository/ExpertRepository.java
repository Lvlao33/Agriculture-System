package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long> {

    List<Expert> findByIsAvailableTrue();

    List<Expert> findBySpecialtiesContaining(String specialty);

    @Query("SELECT e FROM Expert e WHERE e.name LIKE %:keyword% OR e.title LIKE %:keyword% OR e.description LIKE %:keyword%")
    List<Expert> findByKeyword(String keyword);

    List<Expert> findByExperienceYearsGreaterThanEqual(Integer minYears);

    List<Expert> findByNameContainingIgnoreCase(String name);

    List<Expert> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT e FROM Expert e WHERE e.user.id = :userId")
    List<Expert> findByUser_Id(Long userId);
}