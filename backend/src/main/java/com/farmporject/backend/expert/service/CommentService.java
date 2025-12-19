package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.model.Comment;
import com.farmporject.backend.expert.model.Knowledge;
import com.farmporject.backend.expert.repository.CommentRepository;
import com.farmporject.backend.expert.repository.KnowledgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final KnowledgeRepository knowledgeRepository;

    public CommentService(CommentRepository commentRepository,
                          KnowledgeRepository knowledgeRepository) {
        this.commentRepository = commentRepository;
        this.knowledgeRepository = knowledgeRepository;
    }

    public List<Comment> getCommentsByKnowledgeId(Long knowledgeId) {
        return commentRepository.findByKnowledge_KnowledgeId(knowledgeId);
    }

    public Comment addComment(Long knowledgeId, String content) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new RuntimeException("Knowledge not found with id: " + knowledgeId));
        Comment comment = new Comment(knowledge, content);
        return commentRepository.save(comment);
    }
}