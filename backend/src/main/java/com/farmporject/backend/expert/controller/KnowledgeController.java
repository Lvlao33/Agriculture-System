package com.farmporject.backend.expert.controller;

import com.farmporject.backend.expert.model.Knowledge;
import com.farmporject.backend.expert.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 知识模块-农业知识
 * 路由前缀：/api/knowledge
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @Autowired
    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /**
     * 知识列表（分页）
     * 示例：GET /api/knowledge/1?size=10
     */
    @GetMapping("/{pageNum}")
    public ResponseEntity<?> list(@PathVariable Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> response = new HashMap<>();

        try {
            Pageable pageable = PageRequest.of(pageNum - 1, size);
            Page<Knowledge> knowledgePage = knowledgeService.getPublishedKnowledge(pageable);

            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", knowledgePage.getContent());
            data.put("total", knowledgePage.getTotalElements());
            data.put("totalPages", knowledgePage.getTotalPages());
            data.put("currentPage", pageNum);
            data.put("pageSize", size);

            response.put("data", data);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取知识列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 知识列表（无分页，兼容旧接口）
     * 示例：GET /api/knowledge
     */
    @GetMapping
    public ResponseEntity<?> listAll() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 获取前20条记录作为兼容
            Pageable pageable = PageRequest.of(0, 20);
            Page<Knowledge> knowledgePage = knowledgeService.getPublishedKnowledge(pageable);

            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", knowledgePage.getContent());
            data.put("total", knowledgePage.getTotalElements());

            response.put("data", data);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取知识列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 知识详情
     * 示例：GET /api/knowledge/detail/1
     */
    @GetMapping("/detail/{knowledgeId}")
    public ResponseEntity<?> detail(@PathVariable Long knowledgeId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Knowledge> knowledge = knowledgeService.getKnowledgeById(knowledgeId);
            if (knowledge.isPresent()) {
                response.put("flag", true);
                response.put("data", knowledge.get());
            } else {
                response.put("flag", false);
                response.put("message", "知识不存在");
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取知识详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 新增知识
     * 示例：POST /api/knowledge
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Knowledge knowledge) {
        Map<String, Object> response = new HashMap<>();

        try {
            Knowledge createdKnowledge = knowledgeService.createKnowledge(knowledge);
            response.put("flag", true);
            response.put("message", "知识创建成功");
            response.put("data", createdKnowledge);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "创建知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新知识
     * 示例：PUT /api/knowledge/1
     */
    @PutMapping("/{knowledgeId}")
    public ResponseEntity<?> update(@PathVariable Long knowledgeId, @RequestBody Knowledge knowledge) {
        Map<String, Object> response = new HashMap<>();

        try {
            Knowledge updatedKnowledge = knowledgeService.updateKnowledge(knowledgeId, knowledge);
            response.put("flag", true);
            response.put("message", "知识更新成功");
            response.put("data", updatedKnowledge);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            response.put("flag", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "更新知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除知识
     * 示例：DELETE /api/knowledge/1
     */
    @DeleteMapping("/{knowledgeId}")
    public ResponseEntity<?> delete(@PathVariable Long knowledgeId) {
        Map<String, Object> response = new HashMap<>();

        try {
            knowledgeService.deleteKnowledge(knowledgeId);
            response.put("flag", true);
            response.put("message", "知识删除成功");
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            response.put("flag", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "删除知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 搜索知识
     * 示例：GET /api/knowledge/search?keyword=小麦
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> response = new HashMap<>();

        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Knowledge> knowledgePage = knowledgeService.searchKnowledgeByKeyword(keyword, pageable);

            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", knowledgePage.getContent());
            data.put("total", knowledgePage.getTotalElements());
            data.put("totalPages", knowledgePage.getTotalPages());
            data.put("currentPage", page);
            data.put("pageSize", size);
            data.put("keyword", keyword);

            response.put("data", data);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "搜索知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 点赞知识
     * 示例：POST /api/knowledge/1/like
     */
    @PostMapping("/{knowledgeId}/like")
    public ResponseEntity<?> like(@PathVariable Long knowledgeId) {
        Map<String, Object> response = new HashMap<>();

        try {
            knowledgeService.likeKnowledge(knowledgeId);
            response.put("flag", true);
            response.put("message", "点赞成功");
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            response.put("flag", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "点赞失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取热门知识
     * 示例：GET /api/knowledge/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularKnowledge() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Knowledge> popularKnowledge = knowledgeService.getPopularKnowledge();
            response.put("flag", true);
            response.put("data", popularKnowledge);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取热门知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取最新知识
     * 示例：GET /api/knowledge/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentKnowledge() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Knowledge> recentKnowledge = knowledgeService.getRecentKnowledge();
            response.put("flag", true);
            response.put("data", recentKnowledge);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取最新知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有分类
     * 示例：GET /api/knowledge/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> categories = knowledgeService.getAllCategories();
            response.put("flag", true);
            response.put("data", categories);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取分类失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据分类获取知识
     * 示例：GET /api/knowledge/category/种植技术
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getKnowledgeByCategory(@PathVariable String category) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Knowledge> knowledgeList = knowledgeService.getKnowledgeByCategory(category);
            response.put("flag", true);
            response.put("data", knowledgeList);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("flag", false);
            response.put("message", "获取分类知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}