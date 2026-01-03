package com.farmporject.backend.expert.controller;

import com.farmporject.backend.expert.model.Comment;
import com.farmporject.backend.expert.model.Knowledge;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.service.CommentService;
import com.farmporject.backend.expert.service.KnowledgeService;
import com.farmporject.backend.expert.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 农业知识模块 (Robust Version)
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final CommentService commentService;
    private final ExpertService expertService;

    @Autowired
    public KnowledgeController(KnowledgeService knowledgeService,
                               CommentService commentService,
                               ExpertService expertService) {
        this.knowledgeService = knowledgeService;
        this.commentService = commentService;
        this.expertService = expertService;
    }

    // 安全的 DTO 转换方法
    private Map<String, Object> convertToDto(Knowledge k) {
        Map<String, Object> item = new HashMap<>();
        if (k == null) return item;

        item.put("knowledgeId", k.getKnowledgeId());
        item.put("title", k.getTitle());
        item.put("content", k.getContent());
        item.put("summary", k.getSummary());
        item.put("picPath", k.getPicPath());
        item.put("viewCount", k.getViewCount());
        item.put("likeCount", k.getLikeCount());
        item.put("createTime", k.getCreateTime());
        item.put("tags", k.getTags() != null ? k.getTags() : new ArrayList<>());
        item.put("categories", k.getCategories() != null ? k.getCategories() : new ArrayList<>());
        item.put("url", k.getUrl());

        if (k.getAuthor() != null) {
            Map<String, Object> authorDto = new HashMap<>();
            authorDto.put("id", k.getAuthor().getId());
            authorDto.put("name", k.getAuthor().getName());
            authorDto.put("title", k.getAuthor().getTitle());
            authorDto.put("avatar", k.getAuthor().getAvatar());
            item.put("author", authorDto);
            item.put("authorName", k.getAuthor().getName());
        }
        
        return item;
    }

    @GetMapping("/{pageNum}")
    public ResponseEntity<?> list(@PathVariable Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
            Page<Knowledge> page = knowledgeService.getPublishedKnowledge(pageable);

            List<Map<String, Object>> dtoList = new ArrayList<>();
            for (Knowledge k : page.getContent()) {
                dtoList.add(convertToDto(k));
            }

            resp.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", dtoList);
            data.put("total", page.getTotalElements());
            data.put("totalPages", page.getTotalPages());
            resp.put("data", data);

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("flag", false);
            resp.put("message", "System Error: " + e.getMessage());
            // 返回 500 而不是 400，以便区分是客户端错误还是服务端错误
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 知识详情
     */
    @GetMapping("/detail/{knowledgeId}")
    public ResponseEntity<?> detail(@PathVariable Long knowledgeId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Optional<Knowledge> k = knowledgeService.getKnowledgeById(knowledgeId);
            if (k.isEmpty()) {
                resp.put("flag", false);
                resp.put("message", "知识不存在");
                return ResponseEntity.status(404).body(resp);
            }
            
            // 增加阅读量（如果 Service 没做，可以在这里做，或者忽略）
            // knowledgeService.incrementViewCount(knowledgeId);

            resp.put("flag", true);
            resp.put("data", convertToDto(k.get())); // 同样使用 DTO
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("flag", false);
            resp.put("message", "获取详情失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // 兼容旧接口
    @GetMapping("/selectById/{knowledgeId}")
    public ResponseEntity<?> selectById(@PathVariable Long knowledgeId) {
        return detail(knowledgeId);
    }

    @GetMapping("/selectByUsername")
    public ResponseEntity<?> selectByUsername() {
        Map<String, Object> resp = new HashMap<>();
        try {
            Long userId = com.farmporject.backend.security.UserContext.getCurrentUserId();
            if (userId == null) {
                resp.put("flag", false);
                resp.put("message", "用户未登录");
                return ResponseEntity.status(401).body(resp);
            }

            Optional<Expert> expertOpt = expertService.getExpertByUserId(userId);
            if (expertOpt.isEmpty()) {
                resp.put("flag", true);
                resp.put("data", new ArrayList<>());
                return ResponseEntity.ok(resp);
            }

            List<Knowledge> knowledgeList = knowledgeService.getAllKnowledgeByAuthor(expertOpt.get());
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Knowledge k : knowledgeList) {
                resultList.add(convertToDto(k));
            }

            resp.put("flag", true);
            resp.put("data", resultList);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("flag", false);
            resp.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 发布知识
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Knowledge knowledge) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Long userId = com.farmporject.backend.security.UserContext.getCurrentUserId();
            Optional<Expert> expertOpt = expertService.getExpertByUserId(userId);
            
            if (expertOpt.isEmpty()) {
                resp.put("flag", false);
                resp.put("message", "当前账号未在专家表中注册，无法发布知识");
                return ResponseEntity.badRequest().body(resp);
            }
            knowledge.setAuthor(expertOpt.get());
            Knowledge created = knowledgeService.createKnowledge(knowledge);

            resp.put("flag", true);
            resp.put("message", "发布成功");
            resp.put("data", convertToDto(created)); 
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("flag", false);
            resp.put("message", "发布失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 更新知识
     */
    @PutMapping("/{knowledgeId}")
    public ResponseEntity<?> update(@PathVariable Long knowledgeId,
                                    @RequestBody Knowledge knowledge) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Knowledge updated = knowledgeService.updateKnowledge(knowledgeId, knowledge);
            resp.put("flag", true);
            resp.put("message", "知识更新成功");
            resp.put("data", convertToDto(updated));
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            resp.put("flag", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(404).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("flag", false);
            resp.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 删除知识
     */
    @DeleteMapping("/{knowledgeId}")
    public ResponseEntity<?> delete(@PathVariable Long knowledgeId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            knowledgeService.deleteKnowledge(knowledgeId);
            resp.put("flag", true);
            resp.put("message", "删除成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("flag", false);
            resp.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取评论
     */
    @GetMapping("/selectByKnowledge/{knowledgeId}")
    public ResponseEntity<?> getCommentsByKnowledge(@PathVariable Long knowledgeId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Comment> comments = commentService.getCommentsByKnowledgeId(knowledgeId);
            // 评论里可能也有用户实体，如果报错也需要转DTO，暂时先直接返回试试
            resp.put("flag", true);
            resp.put("data", comments);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("flag", false);
            resp.put("message", "获取评论失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 添加评论
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
            e.printStackTrace();
            resp.put("flag", false);
            resp.put("message", "添加评论失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
