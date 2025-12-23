package com.farmporject.backend.expert.controller;

import com.farmporject.backend.expert.model.Comment;
import com.farmporject.backend.expert.model.Knowledge;
import com.farmporject.backend.expert.service.CommentService;
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
 * 农业知识模块
 * 统一前缀：/api/knowledge
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final CommentService commentService;

    @Autowired
    public KnowledgeController(KnowledgeService knowledgeService,
                               CommentService commentService) {
        this.knowledgeService = knowledgeService;
        this.commentService = commentService;
    }

    /**
     * 分页获取知识列表（农业知识页）
     * GET /api/knowledge/{pageNum}?size=10
     */
    @GetMapping("/{pageNum}")
    public ResponseEntity<?> list(@PathVariable Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(pageNum - 1, size);
            Page<Knowledge> page = knowledgeService.getPublishedKnowledge(pageable);

            resp.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", page.getContent());
            data.put("total", page.getTotalElements());
            data.put("totalPages", page.getTotalPages());
            data.put("currentPage", pageNum);
            data.put("pageSize", size);
            resp.put("data", data);

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取知识列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * 知识详情（新接口）
     * GET /api/knowledge/detail/{id}
     */
    @GetMapping("/detail/{knowledgeId}")
    public ResponseEntity<?> detail(@PathVariable Long knowledgeId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Optional<Knowledge> k = knowledgeService.getKnowledgeById(knowledgeId);
            if (k.isEmpty()) {
                resp.put("flag", false);
                resp.put("message", "知识不存在");
                return ResponseEntity.notFound().build();
            }
            resp.put("flag", true);
            resp.put("data", k.get());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取知识详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * 知识详情（旧接口兼容）
     * GET /api/knowledge/selectById/{id}
     */
    @GetMapping("/selectById/{knowledgeId}")
    public ResponseEntity<?> selectById(@PathVariable Long knowledgeId) {
        return detail(knowledgeId);
    }

    /**
     * 新增知识（专家发布）
     * POST /api/knowledge
     * body: { title, content, picPath?, url? , ... }
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Knowledge knowledge) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Knowledge created = knowledgeService.createKnowledge(knowledge);
            resp.put("flag", true);
            resp.put("message", "知识创建成功");
            resp.put("data", created);
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "创建知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * 更新知识
     * PUT /api/knowledge/{id}
     */
    @PutMapping("/{knowledgeId}")
    public ResponseEntity<?> update(@PathVariable Long knowledgeId,
                                    @RequestBody Knowledge knowledge) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Knowledge updated = knowledgeService.updateKnowledge(knowledgeId, knowledge);
            resp.put("flag", true);
            resp.put("message", "知识更新成功");
            resp.put("data", updated);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            resp.put("flag", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "更新知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * 删除知识
     * DELETE /api/knowledge/{id}
     */
    @DeleteMapping("/{knowledgeId}")
    public ResponseEntity<?> delete(@PathVariable Long knowledgeId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            knowledgeService.deleteKnowledge(knowledgeId);
            resp.put("flag", true);
            resp.put("message", "知识删除成功");
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            resp.put("flag", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "删除知识失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * 根据登录用户查询知识（目前先返回已发布列表，占位用）
     * GET /api/knowledge/selectByUsername/
     */
    @GetMapping("/selectByUsername")
    public ResponseEntity<?> selectByUsername() {
        Map<String, Object> resp = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(0, 100);
            Page<Knowledge> page = knowledgeService.getPublishedKnowledge(pageable);

            resp.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", page.getContent());
            data.put("total", page.getTotalElements());
            resp.put("data", data);

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取用户知识列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * 根据知识ID获取评论列表
     * GET /api/knowledge/selectByKnowledge/{knowledgeId}
     */
    @GetMapping("/selectByKnowledge/{knowledgeId}")
    public ResponseEntity<?> getCommentsByKnowledge(@PathVariable Long knowledgeId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Comment> comments = commentService.getCommentsByKnowledgeId(knowledgeId);
            resp.put("flag", true);
            resp.put("data", comments);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取评论列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * 添加评论
     * POST /api/knowledge/addByKnowledge/{knowledgeId}/{content}
     */
    @PostMapping("/addByKnowledge/{knowledgeId}/{content}")
    public ResponseEntity<?> addComment(@PathVariable Long knowledgeId,
                                        @PathVariable String content) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Comment comment = commentService.addComment(knowledgeId, content);
            resp.put("flag", true);
            resp.put("message", "添加评论成功");
            resp.put("data", comment);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            resp.put("flag", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "添加评论失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}