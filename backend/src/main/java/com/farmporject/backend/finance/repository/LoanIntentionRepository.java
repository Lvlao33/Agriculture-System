package com.farmporject.backend.finance.repository;

import com.farmporject.backend.finance.model.LoanIntention;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 融资意向仓库接口
 */
@Repository
public interface LoanIntentionRepository extends JpaRepository<LoanIntention, Long> {

    /**
     * 根据用户ID查找融资意向
     * 
     * @param userId 用户ID
     * @return 融资意向（如果存在）
     */
    @EntityGraph(attributePaths = { "user" })
    Optional<LoanIntention> findByUserId(Long userId);

    /**
     * 查找除指定用户外的所有融资意向
     * 
     * @param userId 要排除的用户ID
     * @return 其他用户的融资意向列表
     */
    @EntityGraph(attributePaths = { "user" })
    List<LoanIntention> findAllByUserIdNot(Long userId);

    /**
     * 删除指定用户的融资意向
     * 
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);
}
