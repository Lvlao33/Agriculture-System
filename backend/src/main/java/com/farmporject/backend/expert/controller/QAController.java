package com.farmporject.backend.expert.controller;

import com.farmporject.backend.common.api.ApiResponse;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.model.Question;
import com.farmporject.backend.expert.service.QAService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专家模块-问答
 * 路由前缀：/api/qa
 * - GET /api/qa/questions 问答列表
 * - POST /api/qa/questions 发起提问（支持附件）
 * - POST /api/qa/questions/{id}/answers 回答问题（专家端）
 */
@RestController
@RequestMapping("/api/qa")
public class QAController {

    private final QAService qaService;

    public QAController(QAService qaService) {
        this.qaService = qaService;
    }

    /**
     * 问答列表
     * mine = true 时返回当前用户自己的提问记录，否则返回全部问题
     */
    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<Question>>> questions(
            @RequestParam(value = "mine", required = false) Boolean mine,
            @RequestHeader(value = "Authorization", required = false) String token) {

        List<Question> list;
        if (Boolean.TRUE.equals(mine) && token != null && token.startsWith("tk_")) {
            String[] parts = token.split("_");
            String userId = parts.length >= 2 ? parts[1] : null;
            list = (userId != null) ? qaService.getUserQuestions(userId) : qaService.getAllQuestions();
        } else {
            list = qaService.getAllQuestions();
        }
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    /**
     * 发起提问（支持图片/视频附件）
     * 示例：POST /api/qa/questions
     */
    @PostMapping(value = "/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Question>> ask(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "expertId", required = false) Long expertId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            // 从 token 中提取用户信息（与 UserController 保持一致：tk_id_username）
            String userId = null;
            String userName = null;
            if (token != null && token.startsWith("tk_")) {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    userId = parts[1];
                    userName = parts[2];
                }
            }

            Question q = new Question();
            q.setTitle(title);
            q.setContent(content);
            q.setUserId(userId != null ? userId : "anonymous");
            q.setUserName(userName != null ? userName : "游客");
            if (expertId != null) {
                Expert expert = new Expert();
                expert.setId(expertId);
                q.setExpert(expert);
            }

            // 先保存问题获取ID
            Question saved = qaService.createQuestion(q);

            // 保存附件到本地，并记录访问路径
            List<String> attachmentUrls = saveQuestionFiles(saved.getId(), files);
            if (!attachmentUrls.isEmpty()) {
                saved = qaService.updateQuestionAttachments(saved.getId(), attachmentUrls);
            }

            return ResponseEntity.status(201).body(ApiResponse.ok(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("保存问题失败: " + e.getMessage()));
        }
    }

    private List<String> saveQuestionFiles(Long questionId, List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return urls;
        }
        String projectRoot = System.getProperty("user.dir");
        String uploadDir = projectRoot + File.separator + "qa_files" + File.separator + questionId;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            String originalName = file.getOriginalFilename();
            Path path = Paths.get(uploadDir, originalName);
            file.transferTo(path.toFile());
            // 暂时返回相对路径，前端可根据实际静态资源映射拼接完整URL
            String url = "/qa_files/" + questionId + "/" + originalName;
            urls.add(url);
        }
        return urls;
    }

    /**
     * 回答问题（保留原有占位实现）
     */
    @PostMapping("/questions/{id}/answers")
    public ResponseEntity<?> answer(@PathVariable String id) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "answered " + id);
        return ResponseEntity.status(201).body(resp);
    }
}
