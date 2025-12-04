package com.farmporject.backend.expert.controller;

import com.farmporject.backend.common.api.ApiResponse;
import com.farmporject.backend.expert.model.Answer;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.model.Question;
import com.farmporject.backend.expert.service.ExpertService;
import com.farmporject.backend.expert.service.QAService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问答模块控制器
 * 根据前端接口规范实现
 */
@RestController
public class QAController {

    private final QAService qaService;
    private final ExpertService expertService;

    public QAController(QAService qaService, ExpertService expertService) {
        this.qaService = qaService;
        this.expertService = expertService;
    }

    /**
     * GET /api/qa/questions
     * 获取问答列表
     * 参数: pageNum, pageSize, mine, keyword
     */
    @GetMapping("/api/qa/questions")
    public ResponseEntity<Map<String, Object>> getQuestionsList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "mine", required = false) Boolean mine,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        try {
            System.out.println("=== 获取问题列表 ===");
            System.out.println("pageNum: " + pageNum + ", pageSize: " + pageSize);
            System.out.println("mine: " + mine + ", keyword: " + keyword);
            System.out.println("token: " + token);
            
            List<Question> list = null;
            try {
            
                // 如果 mine=true，查询当前用户的问题
                if (Boolean.TRUE.equals(mine) && token != null && token.startsWith("tk_")) {
                    String[] parts = token.split("_");
                    String userId = parts.length >= 2 ? parts[1] : null;
                    System.out.println("提取的userId: " + userId);
                    if (userId != null) {
                        list = qaService.getUserQuestions(userId);
                        System.out.println("查询用户问题，数量: " + (list != null ? list.size() : 0));
                    } else {
                        list = qaService.getAllQuestions();
                        System.out.println("userId为null，查询全部问题，数量: " + (list != null ? list.size() : 0));
                    }
                } else {
                    // 查询全部问题
                    list = qaService.getAllQuestions();
                    System.out.println("查询全部问题，数量: " + (list != null ? list.size() : 0));
                }
                
                // 关键词搜索
                if (keyword != null && !keyword.trim().isEmpty()) {
                    System.out.println("执行关键词搜索: " + keyword);
                    list = qaService.searchQuestionsByKeyword(keyword.trim());
                    System.out.println("搜索后问题数量: " + (list != null ? list.size() : 0));
                }
            } catch (Exception queryException) {
                System.err.println("查询数据库时出错: " + queryException.getMessage());
                queryException.printStackTrace();
                throw queryException; // 重新抛出异常，让外层catch处理
            }
            
            if (list == null) {
                list = new ArrayList<>();
            }
            
            // 确保按更新时间降序排列（如果数据库查询没有排序）
            list.sort((a, b) -> {
                if (a.getUpdateTime() != null && b.getUpdateTime() != null) {
                    return b.getUpdateTime().compareTo(a.getUpdateTime());
                } else if (a.getUpdateTime() != null) {
                    return -1;
                } else if (b.getUpdateTime() != null) {
                    return 1;
                } else if (a.getCreateTime() != null && b.getCreateTime() != null) {
                    return b.getCreateTime().compareTo(a.getCreateTime());
                }
                return 0;
            });
            
            // 手动分页
            int total = list.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Question> pagedList = start < total ? list.subList(start, end) : new ArrayList<>();
            
            // 转换为DTO格式，避免懒加载问题
            List<Map<String, Object>> resultList = new ArrayList<>();
            System.out.println("开始转换 " + pagedList.size() + " 条问题数据");
            
            for (int i = 0; i < pagedList.size(); i++) {
                Question q = pagedList.get(i);
                try {
                    System.out.println("处理第 " + (i + 1) + " 条问题，ID: " + q.getId() + ", 标题: " + q.getTitle());
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", q.getId());
                    item.put("title", q.getTitle() != null ? q.getTitle() : "");
                    item.put("content", q.getContent() != null ? q.getContent() : "");
                    item.put("userId", q.getUserId() != null ? q.getUserId() : "");
                    item.put("userName", q.getUserName() != null ? q.getUserName() : "");
                    item.put("status", q.getStatus() != null ? q.getStatus().name() : "PENDING");
                    
                    // 将LocalDateTime转换为字符串，避免序列化问题
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    if (q.getCreateTime() != null) {
                        item.put("createTime", q.getCreateTime().format(formatter));
                    } else {
                        item.put("createTime", null);
                    }
                    if (q.getUpdateTime() != null) {
                        item.put("updateTime", q.getUpdateTime().format(formatter));
                    } else {
                        item.put("updateTime", null);
                    }
                    
                    // 处理附件和标签，避免懒加载异常
                    try {
                        List<String> attachments = q.getAttachmentUrls();
                        item.put("attachmentUrls", attachments != null ? attachments : new ArrayList<>());
                    } catch (Exception e) {
                        System.err.println("获取附件失败 (问题ID: " + q.getId() + "): " + e.getClass().getName() + ": " + e.getMessage());
                        item.put("attachmentUrls", new ArrayList<>());
                    }
                    try {
                        List<String> tags = q.getTags();
                        item.put("tags", tags != null ? tags : new ArrayList<>());
                    } catch (Exception e) {
                        System.err.println("获取标签失败 (问题ID: " + q.getId() + "): " + e.getClass().getName() + ": " + e.getMessage());
                        item.put("tags", new ArrayList<>());
                    }
                    
                    // 暂时不查询答案数量，避免懒加载问题
                    item.put("answerCount", 0);
                    
                    // 处理expert字段，避免懒加载
                    item.put("expert", null);
                    
                    resultList.add(item);
                    System.out.println("成功处理第 " + (i + 1) + " 条问题");
                } catch (Exception e) {
                    System.err.println("处理问题失败 (问题ID: " + q.getId() + "): " + e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    // 即使处理失败，也继续处理下一条
                }
            }
            
            System.out.println("成功转换 " + resultList.size() + " 条问题数据");
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("data", resultList);
            response.put("total", total);
            response.put("pageNum", pageNum);
            response.put("pageSize", pageSize);
            
            System.out.println("返回数据，总数: " + total + ", 当前页: " + resultList.size());
            System.out.println("准备返回响应，响应大小: " + response.size() + " 个键");
            
            // 验证响应数据
            try {
                // 尝试创建一个简单的测试响应，确保序列化正常
                Map<String, Object> testResponse = new HashMap<>();
                testResponse.put("flag", true);
                testResponse.put("test", "ok");
                System.out.println("测试响应创建成功");
            } catch (Exception testEx) {
                System.err.println("测试响应创建失败: " + testEx.getMessage());
                testEx.printStackTrace();
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== 获取问题列表失败 ===");
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("错误类型: " + e.getClass().getName());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "获取问题列表失败: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            // 返回空列表而不是500错误，让前端至少能显示
            response.put("data", new ArrayList<>());
            response.put("total", 0);
            response.put("pageNum", pageNum);
            response.put("pageSize", pageSize);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /qa/question/{id}
     * 获取问题详情
     */
    @GetMapping("/qa/question/{id}")
    public ResponseEntity<Map<String, Object>> getQuestionDetail(@PathVariable Long id) {
        try {
            Question question = qaService.getQuestionById(id)
                    .orElseThrow(() -> new RuntimeException("问题不存在"));
            
            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("id", question.getId());
            questionMap.put("title", question.getTitle());
            questionMap.put("content", question.getContent());
            questionMap.put("userId", question.getUserId());
            questionMap.put("userName", question.getUserName());
            questionMap.put("status", question.getStatus() != null ? question.getStatus().name() : "PENDING");
            questionMap.put("createTime", question.getCreateTime());
            questionMap.put("updateTime", question.getUpdateTime());
            questionMap.put("attachmentUrls", question.getAttachmentUrls());
            questionMap.put("tags", question.getTags());
            
            // 处理expert字段
            if (question.getExpert() != null) {
                try {
                    Expert expert = question.getExpert();
                    Map<String, Object> expertMap = new HashMap<>();
                    expertMap.put("id", expert.getId());
                    expertMap.put("name", expert.getName());
                    expertMap.put("title", expert.getTitle());
                    questionMap.put("expert", expertMap);
                } catch (Exception e) {
                    questionMap.put("expert", null);
                }
            } else {
                questionMap.put("expert", null);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("data", questionMap);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "获取问题详情失败: " + e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * GET /qa/answers
     * 获取回答列表
     * 参数: questionId
     */
    @GetMapping("/qa/answers")
    public ResponseEntity<Map<String, Object>> getAnswersList(
            @RequestParam(value = "questionId", required = true) Long questionId) {
        try {
            List<Answer> answers = qaService.getQuestionAnswers(questionId);
            
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Answer answer : answers) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", answer.getId());
                item.put("content", answer.getContent());
                item.put("createTime", answer.getCreateTime());
                
                // 处理expert字段
                if (answer.getExpert() != null) {
                    try {
                        Expert expert = answer.getExpert();
                        Map<String, Object> expertMap = new HashMap<>();
                        expertMap.put("id", expert.getId());
                        expertMap.put("name", expert.getName());
                        expertMap.put("title", expert.getTitle());
                        item.put("expert", expertMap);
                    } catch (Exception e) {
                        item.put("expert", null);
                    }
                } else {
                    item.put("expert", null);
                }
                
                resultList.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("data", resultList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "获取回答列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * POST /api/qa/questions
     * 提交问题（支持附件）
     */
    @PostMapping(value = "/api/qa/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> submitQuestion(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "expertId", required = false) Long expertId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        try {
            // 从token中提取用户信息
            String userId = null;
            String userName = null;
            if (token != null && token.startsWith("tk_")) {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    userId = parts[1];
                    userName = parts[2];
                } else if (parts.length >= 2) {
                    userId = parts[1];
                }
            }
            
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setUserId(userId != null ? userId : "anonymous");
            question.setUserName(userName != null ? userName : "游客");
            
            if (expertId != null) {
                Expert expert = new Expert();
                expert.setId(expertId);
                question.setExpert(expert);
            }
            
            // 保存问题
            Question saved = qaService.createQuestion(question);
            
            // 保存附件
            if (files != null && !files.isEmpty()) {
                List<String> attachmentUrls = saveQuestionFiles(saved.getId(), files);
                if (!attachmentUrls.isEmpty()) {
                    saved = qaService.updateQuestionAttachments(saved.getId(), attachmentUrls);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("message", "问题提交成功");
            response.put("data", saved);
            
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "提交问题失败: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * POST /qa/answer
     * 提交回答
     */
    @PostMapping("/qa/answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        try {
            Long questionId = Long.valueOf(request.get("questionId").toString());
            String content = request.get("content").toString();
            Long expertId = request.get("expertId") != null ? Long.valueOf(request.get("expertId").toString()) : null;
            
            // 从token中提取专家信息（如果是专家回答）
            if (expertId == null && token != null && token.startsWith("tk_")) {
                String[] parts = token.split("_");
                if (parts.length >= 2) {
                    // 这里需要根据实际情况获取专家ID，暂时使用占位
                }
            }
            
            Answer answer = new Answer();
            Question question = new Question();
            question.setId(questionId);
            answer.setQuestion(question);
            
            if (expertId != null) {
                Expert expert = new Expert();
                expert.setId(expertId);
                answer.setExpert(expert);
            }
            
            answer.setContent(content);
            
            Answer saved = qaService.createAnswer(answer);
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("message", "回答提交成功");
            response.put("data", saved);
            
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "提交回答失败: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * GET /api/qa/test
     * 测试接口：直接查询数据库，返回原始数据
     */
    @GetMapping("/api/qa/test")
    public ResponseEntity<Map<String, Object>> testQuery() {
        try {
            System.out.println("=== 测试查询 ===");
            List<Question> allQuestions = qaService.getAllQuestions();
            System.out.println("数据库中的问题总数: " + allQuestions.size());
            
            if (!allQuestions.isEmpty()) {
                Question first = allQuestions.get(0);
                System.out.println("第一个问题: id=" + first.getId() + ", title=" + first.getTitle() + ", userId=" + first.getUserId());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("total", allQuestions.size());
            response.put("message", "查询成功，共 " + allQuestions.size() + " 条记录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("测试查询失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /qa/experts
     * 获取专家列表
     */
    @GetMapping("/qa/experts")
    public ResponseEntity<Map<String, Object>> getExpertList() {
        try {
            List<Expert> experts = qaService.getAllExperts();
            
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Expert expert : experts) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", expert.getId());
                item.put("name", expert.getName());
                item.put("title", expert.getTitle());
                item.put("avatar", expert.getAvatar());
                item.put("description", expert.getDescription());
                item.put("specialties", expert.getSpecialties());
                item.put("isAvailable", expert.getIsAvailable());
                resultList.add(item);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("data", resultList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "获取专家列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * DELETE /api/qa/questions/{id}
     * 删除问题
     */
    @DeleteMapping("/api/qa/questions/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long id) {
        try {
            System.out.println("=== 删除问题 ===");
            System.out.println("问题ID: " + id);
            
            qaService.deleteQuestion(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("message", "问题删除成功");
            
            System.out.println("问题删除成功，ID: " + id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("删除问题失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "删除问题失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /question/findExpert/{pageNum}
     * 获取专家列表（分页，用于前端专家指导页面）
     */
    @GetMapping("/question/findExpert/{pageNum}")
    public ResponseEntity<Map<String, Object>> findExpert(
            @PathVariable Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            System.out.println("=== 获取专家列表（分页） ===");
            System.out.println("pageNum: " + pageNum + ", pageSize: " + pageSize);
            
            // 获取所有专家
            List<Expert> allExperts = expertService.getAllExperts();
            System.out.println("查询到专家总数: " + allExperts.size());
            
            // 手动分页
            int total = allExperts.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Expert> pagedList = start < total ? allExperts.subList(start, end) : new ArrayList<>();
            
            // 转换为前端需要的格式
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Expert expert : pagedList) {
                try {
                    Map<String, Object> item = new HashMap<>();
                    // 映射为前端需要的字段格式
                    item.put("realName", expert.getName() != null ? expert.getName() : "");
                    item.put("position", expert.getTitle() != null ? expert.getTitle() : "");
                    item.put("profession", expert.getTitle() != null ? expert.getTitle() : ""); // 专业使用 title
                    item.put("belong", expert.getDescription() != null ? expert.getDescription() : ""); // 单位使用 description
                    item.put("phone", expert.getContactInfo() != null ? expert.getContactInfo() : "");
                    item.put("userName", expert.getId() != null ? "expert_" + expert.getId() : ""); // 用于提问和预约
                    item.put("id", expert.getId());
                    item.put("avatar", expert.getAvatar());
                    item.put("isAvailable", expert.getIsAvailable() != null ? expert.getIsAvailable() : true);
                    
                    // 处理 specialties 集合，避免懒加载异常
                    try {
                        List<String> specialties = expert.getSpecialties();
                        item.put("specialties", specialties != null ? specialties : new ArrayList<>());
                    } catch (Exception e) {
                        System.err.println("获取专家专长失败 (专家ID: " + expert.getId() + "): " + e.getMessage());
                        item.put("specialties", new ArrayList<>());
                    }
                    
                    resultList.add(item);
                    System.out.println("成功处理专家: " + expert.getName());
                } catch (Exception e) {
                    System.err.println("处理专家失败 (专家ID: " + expert.getId() + "): " + e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("成功转换 " + resultList.size() + " 个专家数据");
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", resultList);
            data.put("total", total);
            response.put("data", data);
            
            System.out.println("返回数据，总数: " + total + ", 当前页: " + resultList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== 获取专家列表失败 ===");
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("错误类型: " + e.getClass().getName());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "获取专家列表失败: " + e.getMessage());
            Map<String, Object> data = new HashMap<>();
            data.put("list", new ArrayList<>());
            data.put("total", 0);
            response.put("data", data);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /question/findExpertByKeys/{keys}/{pageNum}
     * 根据关键词搜索专家列表（分页）
     */
    @GetMapping("/question/findExpertByKeys/{keys}/{pageNum}")
    public ResponseEntity<Map<String, Object>> findExpertByKeys(
            @PathVariable String keys,
            @PathVariable Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            System.out.println("=== 搜索专家列表 ===");
            System.out.println("关键词: " + keys + ", pageNum: " + pageNum + ", pageSize: " + pageSize);
            
            // 根据关键词搜索专家
            List<Expert> allExperts = expertService.searchExpertsByKeyword(keys);
            System.out.println("搜索到专家总数: " + allExperts.size());
            
            // 手动分页
            int total = allExperts.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Expert> pagedList = start < total ? allExperts.subList(start, end) : new ArrayList<>();
            
            // 转换为前端需要的格式
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Expert expert : pagedList) {
                try {
                    Map<String, Object> item = new HashMap<>();
                    item.put("realName", expert.getName() != null ? expert.getName() : "");
                    item.put("position", expert.getTitle() != null ? expert.getTitle() : "");
                    item.put("profession", expert.getTitle() != null ? expert.getTitle() : "");
                    item.put("belong", expert.getDescription() != null ? expert.getDescription() : "");
                    item.put("phone", expert.getContactInfo() != null ? expert.getContactInfo() : "");
                    item.put("userName", expert.getId() != null ? "expert_" + expert.getId() : "");
                    item.put("id", expert.getId());
                    item.put("avatar", expert.getAvatar());
                    item.put("isAvailable", expert.getIsAvailable() != null ? expert.getIsAvailable() : true);
                    
                    try {
                        List<String> specialties = expert.getSpecialties();
                        item.put("specialties", specialties != null ? specialties : new ArrayList<>());
                    } catch (Exception e) {
                        item.put("specialties", new ArrayList<>());
                    }
                    
                    resultList.add(item);
                } catch (Exception e) {
                    System.err.println("处理专家失败 (专家ID: " + expert.getId() + "): " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", resultList);
            data.put("total", total);
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("搜索专家列表失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "搜索专家列表失败: " + e.getMessage());
            Map<String, Object> data = new HashMap<>();
            data.put("list", new ArrayList<>());
            data.put("total", 0);
            response.put("data", data);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 保存问题附件
     */
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
            String url = "/qa_files/" + questionId + "/" + originalName;
            urls.add(url);
        }
        
        return urls;
    }
}
