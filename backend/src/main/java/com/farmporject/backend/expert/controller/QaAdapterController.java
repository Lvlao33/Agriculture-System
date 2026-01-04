package com.farmporject.backend.expert.controller;

import com.farmporject.backend.expert.model.Answer;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.model.Question;
import com.farmporject.backend.expert.service.ExpertService;
import com.farmporject.backend.expert.service.QAService;
import com.farmporject.backend.security.UserContext;
import com.farmporject.backend.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 兼容前端 /api/qa/* 路径的适配器，调用现有 QAService/ExpertService 实现
 */
@RestController
@RequestMapping("/api/qa")
public class QaAdapterController {

    private final QAService qaService;
    private final ExpertService expertService;

    public QaAdapterController(QAService qaService, ExpertService expertService) {
        this.qaService = qaService;
        this.expertService = expertService;
    }

    /**
     * 列表（支持分页和关键字过滤）
     */
    @GetMapping("/questions")
    public ResponseEntity<Map<String, Object>> listQuestions(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Question> list;
            if (keyword != null && !keyword.isEmpty()) {
                list = qaService.searchQuestionsByKeyword(keyword);
            } else {
                list = qaService.getAllQuestions();
            }

            int total = list.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Question> paged = (start < total) ? list.subList(start, end) : new ArrayList<>();

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Question q : paged) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", q.getId());
                item.put("title", q.getTitle());
                item.put("content", q.getContent());
                if (q.getUser() != null) {
                    item.put("userName", q.getUser().getUsername());
                    item.put("userId", q.getUser().getId());
                } else {
                    item.put("userName", "匿名");
                }
                item.put("status", q.getStatus());
                resultList.add(item);
            }

            resp.put("flag", true);
            resp.put("data", resultList);
            resp.put("total", total);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * 问题详情
     */
    @GetMapping("/question/{id}")
    public ResponseEntity<Map<String, Object>> getQuestion(@PathVariable Long id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Optional<Question> qOpt = qaService.getQuestionById(id);
            if (qOpt.isPresent()) {
                resp.put("flag", true);
                resp.put("data", qOpt.get());
                return ResponseEntity.ok(resp);
            } else {
                resp.put("flag", false);
                resp.put("message", "问题未找到");
                return ResponseEntity.status(404).body(resp);
            }
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取回答列表
     */
    @GetMapping("/answers")
    public ResponseEntity<Map<String, Object>> getAnswers(@RequestParam Long questionId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Answer> answers = qaService.getQuestionAnswers(questionId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Answer a : answers) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", a.getId());
                m.put("content", a.getContent());
                m.put("createTime", a.getCreateTime());
                if (a.getExpert() != null) {
                    m.put("expertName", a.getExpert().getName());
                    m.put("expertTitle", a.getExpert().getTitle());
                }
                result.add(m);
            }
            resp.put("flag", true);
            resp.put("data", result);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取回答失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 提交问题（简化版，支持 form-data 或 application/x-www-form-urlencoded）
     */
    @PostMapping("/questions")
    public ResponseEntity<Map<String, Object>> submitQuestion(@RequestParam Map<String, String> params) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String title = params.get("title");
            String content = params.get("content");
            String picPath = params.get("picPath"); // 可选

            if (title == null || content == null) {
                resp.put("flag", false);
                resp.put("message", "title/content 为必填");
                return ResponseEntity.badRequest().body(resp);
            }

            Question q = new Question();
            q.setTitle(title);
            q.setContent(content);
            if (picPath != null && !picPath.isEmpty()) {
                q.setAttachmentUrls(Collections.singletonList(picPath));
            }
            // 若已登录，设置 user id
            Long userId = UserContext.getCurrentUserId();
            if (userId != null) {
                User u = new User();
                u.setId(userId);
                q.setUser(u);
            }

            Question created = qaService.createQuestion(q);
            resp.put("flag", true);
            resp.put("data", created);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "提交失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 提交回答（专家） - 接受 JSON: { questionId: x, content: "..." }
     */
    @PostMapping("/answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> body) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Object qidObj = body.get("questionId");
            Object contentObj = body.get("content");
            if (qidObj == null || contentObj == null) {
                resp.put("flag", false);
                resp.put("message", "questionId/content 必须提供");
                return ResponseEntity.badRequest().body(resp);
            }
            Long questionId = Long.valueOf(String.valueOf(qidObj));
            String content = String.valueOf(contentObj);

            // 仅允许专家提交回答：通过 UserContext 查找当前用户对应的 Expert
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                resp.put("flag", false);
                resp.put("message", "请先登录后再回答");
                return ResponseEntity.status(401).body(resp);
            }

            Optional<Expert> expertOpt = expertService.getExpertByUserId(userId);
            if (expertOpt.isEmpty()) {
                resp.put("flag", false);
                resp.put("message", "当前用户不是专家，无法回答");
                return ResponseEntity.status(403).body(resp);
            }

            Question q = qaService.getQuestionById(questionId).orElseThrow(() -> new RuntimeException("问题未找到"));

            Answer a = new Answer();
            a.setQuestion(q);
            a.setExpert(expertOpt.get());
            a.setContent(content);

            Answer created = qaService.createAnswer(a);
            resp.put("flag", true);
            resp.put("data", created);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "提交回答失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取专家列表（前端用于选择专家）
     */
    @GetMapping("/experts")
    public ResponseEntity<Map<String, Object>> getExperts() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Expert> experts = expertService.getAvailableExperts();
            resp.put("flag", true);
            resp.put("data", experts);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "获取专家失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}


