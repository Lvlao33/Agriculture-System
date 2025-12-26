package com.farmporject.backend.expert.service;

import com.farmporject.backend.expert.model.Knowledge;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.dto.KnowledgeDTO;
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

    // 鍒嗛〉鑾峰彇宸插彂甯冪殑鐭ヨ瘑鍒楄〃
    public Page<Knowledge> getPublishedKnowledge(Pageable pageable) {
        return knowledgeRepository.findByIsPublishedTrue(pageable);
    }

    // 鑾峰彇鎵€鏈夌煡璇嗭紙鍖呭惈鏈彂甯冪殑锛岀鐞嗗憳鐢級
    public Page<Knowledge> getAllKnowledge(Pageable pageable) {
        return knowledgeRepository.findAll(pageable);
    }

    // 鏍规嵁鍙戝竷鐘舵€佽幏鍙栫煡璇?
    public Page<Knowledge> getKnowledgeByPublishStatus(Boolean isPublished, Pageable pageable) {
        return knowledgeRepository.findByIsPublished(isPublished, pageable);
    }

    // 鏍规嵁ID鑾峰彇鐭ヨ瘑璇︽儏
    public Optional<Knowledge> getKnowledgeById(Long knowledgeId) {
        Optional<Knowledge> knowledge = knowledgeRepository.findById(knowledgeId);

        // 澧炲姞娴忚閲?
        knowledge.ifPresent(k -> knowledgeRepository.incrementViewCount(knowledgeId));

        return knowledge;
    }

    // 鍒涘缓鐭ヨ瘑
    public Knowledge createKnowledge(Knowledge knowledge) {
        // 璁剧疆榛樿鍊?
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

    // 鏇存柊鐭ヨ瘑
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

    // 鏇存柊鍙戝竷鐘舵€?
    public Knowledge updatePublishStatus(Long knowledgeId, Boolean isPublished) {
        return knowledgeRepository.findById(knowledgeId)
                .map(knowledge -> {
                    knowledge.setIsPublished(isPublished);
                    return knowledgeRepository.save(knowledge);
                })
                .orElseThrow(() -> new RuntimeException("Knowledge not found with id: " + knowledgeId));
    }

    // 鍒犻櫎鐭ヨ瘑
    public void deleteKnowledge(Long knowledgeId) {
        if (knowledgeRepository.existsById(knowledgeId)) {
            knowledgeRepository.deleteById(knowledgeId);
        } else {
            throw new RuntimeException("Knowledge not found with id: " + knowledgeId);
        }
    }

    // 鍏抽敭璇嶆悳绱?
    public List<Knowledge> searchKnowledgeByKeyword(String keyword) {
        return knowledgeRepository.findByKeyword(keyword);
    }

    // 鍒嗛〉鍏抽敭璇嶆悳绱?
    public Page<Knowledge> searchKnowledgeByKeyword(String keyword, Pageable pageable) {
        return knowledgeRepository.findByKeyword(keyword, pageable);
    }

    // 鏍规嵁鍒嗙被鏌ヨ
    public List<Knowledge> getKnowledgeByCategory(String category) {
        return knowledgeRepository.findByCategoriesContainingAndIsPublishedTrue(category);
    }

    // 鏍规嵁鏍囩鏌ヨ
    public List<Knowledge> getKnowledgeByTag(String tag) {
        return knowledgeRepository.findByTagsContainingAndIsPublishedTrue(tag);
    }

    // 鏍规嵁浣滆€呮煡璇?
    public List<Knowledge> getKnowledgeByAuthor(Expert author) {
        return knowledgeRepository.findByAuthorAndIsPublishedTrue(author);
    }

    // 鑾峰彇鐑棬鐭ヨ瘑
    public List<Knowledge> getPopularKnowledge() {
        return knowledgeRepository.findTop10ByIsPublishedTrueOrderByViewCountDesc();
    }

    // 获取最新知识
    // 用于首页展示
    public List<KnowledgeDTO> getRecentKnowledge() {
        return knowledgeRepository.findTop5ByIsPublishedTrueOrderByCreateTimeDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private KnowledgeDTO toDTO(Knowledge knowledge) {
        if (knowledge == null)
            return null;
        KnowledgeDTO dto = new KnowledgeDTO();
        dto.setKnowledgeId(knowledge.getKnowledgeId());
        dto.setTitle(knowledge.getTitle());
        dto.setContent(knowledge.getContent());
        dto.setSummary(knowledge.getSummary());
        dto.setCategories(knowledge.getCategories());
        dto.setTags(knowledge.getTags());
        dto.setSource(knowledge.getSource());
        dto.setPicPath(knowledge.getPicPath());
        dto.setUrl(knowledge.getUrl());
        dto.setViewCount(knowledge.getViewCount());
        dto.setLikeCount(knowledge.getLikeCount());
        dto.setIsPublished(knowledge.getIsPublished());
        dto.setCreateTime(knowledge.getCreateTime());
        dto.setUpdateTime(knowledge.getUpdateTime());

        if (knowledge.getAuthor() != null) {
            dto.setAuthorId(knowledge.getAuthor().getId());
            dto.setAuthorName(knowledge.getAuthor().getName());
            dto.setAuthorAvatar(knowledge.getAuthor().getAvatar());
        }

        return dto;
    }

    // 鐐硅禐鐭ヨ瘑
    public void likeKnowledge(Long knowledgeId) {
        if (!knowledgeRepository.existsById(knowledgeId)) {
            throw new RuntimeException("Knowledge not found with id: " + knowledgeId);
        }
        knowledgeRepository.incrementLikeCount(knowledgeId);
    }

    // 鍙栨秷鐐硅禐
    public void unlikeKnowledge(Long knowledgeId) {
        if (!knowledgeRepository.existsById(knowledgeId)) {
            throw new RuntimeException("Knowledge not found with id: " + knowledgeId);
        }
        knowledgeRepository.decrementLikeCount(knowledgeId);
    }

    // 鑾峰彇鎵€鏈夊垎绫?
    public List<String> getAllCategories() {
        return knowledgeRepository.findAllCategories();
    }

    // 鑾峰彇鎵€鏈夋爣绛?
    public List<String> getAllTags() {
        return knowledgeRepository.findAllTags();
    }

    // 缁熻鍒嗙被涓嬬殑鐭ヨ瘑鏁伴噺
    public long countByCategory(String category) {
        return knowledgeRepository.countByCategory(category);
    }

    // 缁熻鏍囩涓嬬殑鐭ヨ瘑鏁伴噺
    public long countByTag(String tag) {
        return knowledgeRepository.countByTag(tag);
    }

    // 鑾峰彇鐭ヨ瘑鎬绘暟
    public long countAllKnowledge() {
        return knowledgeRepository.count();
    }

    // 鑾峰彇宸插彂甯冪煡璇嗘€绘暟
    public long countPublishedKnowledge() {
        return knowledgeRepository.findByIsPublishedTrue(Pageable.unpaged()).getTotalElements();
    }

    // 妫€鏌ョ煡璇嗘槸鍚﹀瓨鍦?
    public boolean existsById(Long knowledgeId) {
        return knowledgeRepository.existsById(knowledgeId);
    }
}