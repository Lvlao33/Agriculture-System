package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.model.Knowledge;
import com.farmporject.backend.expert.repository.KnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;

    @Autowired
    public KnowledgeService(KnowledgeRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
    }

    // 分页获取已发布的知识列表
    public Page<Knowledge> getPublishedKnowledge(Pageable pageable) {
        return knowledgeRepository.findByIsPublishedTrue(pageable);
    }

    // 获取所有知识（包含未发布的，管理员用）
    public Page<Knowledge> getAllKnowledge(Pageable pageable) {
        return knowledgeRepository.findAll(pageable);
    }

    // 根据发布状态获取知识
    public Page<Knowledge> getKnowledgeByPublishStatus(Boolean isPublished, Pageable pageable) {
        return knowledgeRepository.findByIsPublished(isPublished, pageable);
    }

    // 根据ID获取知识详情
    public Optional<Knowledge> getKnowledgeById(Long knowledgeId) {
        Optional<Knowledge> knowledge = knowledgeRepository.findById(knowledgeId);

        // 增加浏览量
        knowledge.ifPresent(k -> knowledgeRepository.incrementViewCount(knowledgeId));

        return knowledge;
    }

    // 创建知识
    public Knowledge createKnowledge(Knowledge knowledge) {
        // 设置默认值
        if (knowledge.getViewCount() == null) {
            knowledge.setViewCount(0);
        }
        if (knowledge.getLikeCount() == null) {
            knowledge.setLikeCount(0);
        }
        if (knowledge.getIsPublished() == null) {
            knowledge.setIsPublished(true);
        }

        return knowledgeRepository.save(knowledge);
    }

    // 更新知识
    public Knowledge updateKnowledge(Long knowledgeId, Knowledge knowledgeDetails) {
        return knowledgeRepository.findById(knowledgeId)
                .map(knowledge -> {
                    if (knowledgeDetails.getTitle() != null) {
                        knowledge.setTitle(knowledgeDetails.getTitle());
                    }
                    if (knowledgeDetails.getContent() != null) {
                        knowledge.setContent(knowledgeDetails.getContent());
                    }
                    if (knowledgeDetails.getSummary() != null) {
                        knowledge.setSummary(knowledgeDetails.getSummary());
                    }
                    if (knowledgeDetails.getCategories() != null) {
                        knowledge.setCategories(knowledgeDetails.getCategories());
                    }
                    if (knowledgeDetails.getTags() != null) {
                        knowledge.setTags(knowledgeDetails.getTags());
                    }
                    if (knowledgeDetails.getAuthor() != null) {
                        knowledge.setAuthor(knowledgeDetails.getAuthor());
                    }
                    if (knowledgeDetails.getSource() != null) {
                        knowledge.setSource(knowledgeDetails.getSource());
                    }
                    if (knowledgeDetails.getIsPublished() != null) {
                        knowledge.setIsPublished(knowledgeDetails.getIsPublished());
                    }
                    return knowledgeRepository.save(knowledge);
                })
                .orElseThrow(() -> new RuntimeException("Knowledge not found with id: " + knowledgeId));
    }

    // 更新发布状态
    public Knowledge updatePublishStatus(Long knowledgeId, Boolean isPublished) {
        return knowledgeRepository.findById(knowledgeId)
                .map(knowledge -> {
                    knowledge.setIsPublished(isPublished);
                    return knowledgeRepository.save(knowledge);
                })
                .orElseThrow(() -> new RuntimeException("Knowledge not found with id: " + knowledgeId));
    }

    // 删除知识
    public void deleteKnowledge(Long knowledgeId) {
        if (knowledgeRepository.existsById(knowledgeId)) {
            knowledgeRepository.deleteById(knowledgeId);
        } else {
            throw new RuntimeException("Knowledge not found with id: " + knowledgeId);
        }
    }

    // 关键词搜索
    public List<Knowledge> searchKnowledgeByKeyword(String keyword) {
        return knowledgeRepository.findByKeyword(keyword);
    }

    // 分页关键词搜索
    public Page<Knowledge> searchKnowledgeByKeyword(String keyword, Pageable pageable) {
        return knowledgeRepository.findByKeyword(keyword, pageable);
    }

    // 根据分类查询
    public List<Knowledge> getKnowledgeByCategory(String category) {
        return knowledgeRepository.findByCategoriesContainingAndIsPublishedTrue(category);
    }

    // 根据标签查询
    public List<Knowledge> getKnowledgeByTag(String tag) {
        return knowledgeRepository.findByTagsContainingAndIsPublishedTrue(tag);
    }

    // 根据作者查询
    public List<Knowledge> getKnowledgeByAuthor(String author) {
        return knowledgeRepository.findByAuthorAndIsPublishedTrue(author);
    }

    // 获取热门知识
    public List<Knowledge> getPopularKnowledge() {
        return knowledgeRepository.findTop10ByIsPublishedTrueOrderByViewCountDesc();
    }

    // 获取最新知识
    public List<Knowledge> getRecentKnowledge() {
        return knowledgeRepository.findTop10ByIsPublishedTrueOrderByCreateTimeDesc();
    }

    // 点赞知识
    public void likeKnowledge(Long knowledgeId) {
        if (!knowledgeRepository.existsById(knowledgeId)) {
            throw new RuntimeException("Knowledge not found with id: " + knowledgeId);
        }
        knowledgeRepository.incrementLikeCount(knowledgeId);
    }

    // 取消点赞
    public void unlikeKnowledge(Long knowledgeId) {
        if (!knowledgeRepository.existsById(knowledgeId)) {
            throw new RuntimeException("Knowledge not found with id: " + knowledgeId);
        }
        knowledgeRepository.decrementLikeCount(knowledgeId);
    }

    // 获取所有分类
    public List<String> getAllCategories() {
        return knowledgeRepository.findAllCategories();
    }

    // 获取所有标签
    public List<String> getAllTags() {
        return knowledgeRepository.findAllTags();
    }

    // 统计分类下的知识数量
    public long countByCategory(String category) {
        return knowledgeRepository.countByCategory(category);
    }

    // 统计标签下的知识数量
    public long countByTag(String tag) {
        return knowledgeRepository.countByTag(tag);
    }

    // 获取知识总数
    public long countAllKnowledge() {
        return knowledgeRepository.count();
    }

    // 获取已发布知识总数
    public long countPublishedKnowledge() {
        return knowledgeRepository.findByIsPublishedTrue(Pageable.unpaged()).getTotalElements();
    }

    // 检查知识是否存在
    public boolean existsById(Long knowledgeId) {
        return knowledgeRepository.existsById(knowledgeId);
    }
}