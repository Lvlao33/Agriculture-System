package com.farmporject.backend.expert.repository;

import com.farmporject.backend.expert.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByKnowledge_KnowledgeId(Long knowledgeId);
}