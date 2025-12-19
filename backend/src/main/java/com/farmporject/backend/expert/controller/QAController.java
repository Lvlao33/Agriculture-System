package com.farmporject.backend.expert.controller;

import com.farmporject.backend.expert.dto.AnswerDTO;
import com.farmporject.backend.expert.model.Answer;
import com.farmporject.backend.expert.model.Expert;
import com.farmporject.backend.expert.model.Question;
import com.farmporject.backend.expert.service.QAService;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.service.UserService;
import com.farmporject.backend.expert.service.ExpertService;
import com.farmporject.backend.expert.service.AnswerService;
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
 * 问答模块控制�??
 * 根据前端接口规范实现
 */
@RestController
@RequestMapping("/api/qa")
public class QAController {

    private final UserService userService;
    private final ExpertService expertService;
    private final QAService qaService;
    private final AnswerService answerService;

    public QAController(UserService userService, QAService qaService, ExpertService expertService,
            AnswerService answerService) {
        this.userService = userService;
        this.expertService = expertService;
        this.qaService = qaService;
        this.answerService = answerService;
    }

    /**
     * GET /api/qa/questions
     * 获取问答列表，包含分页显�??
     * 参数: pageNum, pageSize, mine, keyword
     */
    @GetMapping("/questions")
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
                        Long uid = Long.parseLong(userId);
                        list = qaService.getUserQuestions(uid);
                        System.out.println("查询用户问题，数�??: " + (list != null ? list.size() : 0));
                    } else {
                        list = qaService.getAllQuestions();
                        System.out.println("userId为null，查询全部问题，数量: " + (list != null ? list.size() : 0));
                    }
                } else {
                    // 查询全部问题
                    list = qaService.getAllQuestions();
                    System.out.println("查询全部问题，数�??: " + (list != null ? list.size() : 0));
                }

                // 关键词搜�??
                if (keyword != null && !keyword.trim().isEmpty()) {
                    System.out.println("执行关键词搜�??: " + keyword);
                    list = qaService.searchQuestionsByKeyword(keyword.trim());
                    System.out.println("搜索后问题数�??: " + (list != null ? list.size() : 0));
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
            System.out.println("开始转�?? " + pagedList.size() + " 条问题数�??");

            for (int i = 0; i < pagedList.size(); i++) {
                Question q = pagedList.get(i);
                try {
                    System.out.println("处理�?? " + (i + 1) + " 条问题，ID: " + q.getId() + ", 标题: " + q.getTitle());

                    Map<String, Object> item = new HashMap<>();
                    item.put("id", q.getId());
                    item.put("title", q.getTitle() != null ? q.getTitle() : "");
                    item.put("content", q.getContent() != null ? q.getContent() : "");
                    // 处理用户字段，避免懒加载问题
                    try {
                        if (q.getUser() != null) {
                            User user = q.getUser();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("id", user.getId());
                            userMap.put("username", user.getUsername());
                            userMap.put("nickname", user.getNickname());
                            userMap.put("avatar", user.getAvatar());
                            item.put("user", userMap);
                            item.put("userId", user.getId()); // 保持兼容�??
                            item.put("userName", user.getUsername()); // 保持兼容�??
                        } else {
                            item.put("user", null);
                            item.put("userId", null);
                            item.put("userName", "匿名用户");
                        }
                    } catch (Exception e) {
                        // 懒加载失败，使用默认�??
                        item.put("user", null);
                        item.put("userId", null);
                        item.put("userName", "匿名用户");
                    }
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

                    // 处理附件和标签，避免懒加载异�??
                    try {
                        List<String> attachments = q.getAttachmentUrls();
                        item.put("attachmentUrls", attachments != null ? attachments : new ArrayList<>());
                    } catch (Exception e) {
                        System.err.println(
                                "获取附件失败 (问题ID: " + q.getId() + "): " + e.getClass().getName() + ": " + e.getMessage());
                        item.put("attachmentUrls", new ArrayList<>());
                    }
                    try {
                        List<String> tags = q.getTags();
                        item.put("tags", tags != null ? tags : new ArrayList<>());
                    } catch (Exception e) {
                        System.err.println(
                                "获取标签失败 (问题ID: " + q.getId() + "): " + e.getClass().getName() + ": " + e.getMessage());
                        item.put("tags", new ArrayList<>());
                    }
            
                    // 查询答案数量
                    try {
                        int answerCount = qaService.getQuestionAnswersCount(q.getId());
                        item.put("answerCount", answerCount);
                    } catch (Exception e) {
                        item.put("answerCount", 0);
                    }

                    // 处理expert字段，避免懒加载
                    item.put("expert", null);

                    resultList.add(item);
                    System.out.println("成功处理�?? " + (i + 1) + " 条问�??");
                } catch (Exception e) {
                    System.err.println(
                            "处理问题失败 (问题ID: " + q.getId() + "): " + e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    // 即使处理失败，也继续处理下一�??
                }
            }

            System.out.println("成功转换 " + resultList.size() + " 条问题数�??");

            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("data", resultList);
            response.put("total", total);
            response.put("pageNum", pageNum);
            response.put("pageSize", pageSize);

            System.out.println("返回数据，总数: " + total + ", 当前�??: " + resultList.size());
            System.out.println("准备返回响应，响应大�??: " + response.size() + " 个键");

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

            // 返回空列表而不�??500错误，让前端至少能显�??
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
    @GetMapping("/question/{id}")
    public ResponseEntity<Map<String, Object>> getQuestionDetail(@PathVariable Long id) {
        try {
            Question question = qaService.getQuestionById(id)
                    .orElseThrow(() -> new RuntimeException("问题不存�??"));

            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("id", question.getId());
            questionMap.put("title", question.getTitle());
            questionMap.put("content", question.getContent());
            // 处理用户字段
            try {
                if (question.getUser() != null) {
                    User user = question.getUser();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("nickname", user.getNickname());
                    userMap.put("avatar", user.getAvatar());
                    questionMap.put("user", userMap);
                    questionMap.put("userName", user.getUsername());
                } else {
                    questionMap.put("user", null);
                    questionMap.put("userName", "匿名用户");
                }
            } catch (Exception e) {
                // 懒加载失败，使用默认�??
                questionMap.put("user", null);
                questionMap.put("userName", "匿名用户");
            }
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
    @GetMapping("/answers")
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
    @PostMapping(value = "/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> submitQuestion(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "expertId", required = false) Long expertId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            // 从token中提取用户信�??
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

            // 创建问题对象
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            // 登录才能提问
            if (userId != null) {
                Long uid = Long.parseLong(userId);
                List<User> users = userService.findUserById(uid).stream().toList();
                if (users.isEmpty()) {
                    throw new RuntimeException("用户不存在，无法提交问题");
                } else {
                    User user = users.get(0);
                    question.setUser(user);
                    // 如果提供了userName且与当前用户名不同，则更�??
                    if (userName != null && !userName.isEmpty() && !userName.equals(user.getUsername())) {
                        user.setUsername(userName);
                    }
                }
            }
            // User user = new User();
            // question.setUser(user);
            // question.getUser().setId(userId != null ? userId : "");
            // question.getUser().setUsername(userName != null ? userName : "游客");

            if (expertId != null) {
                Expert expert = expertService.getExpertById(expertId)
                        .orElseThrow(() -> new RuntimeException("指定的专家不存在"));
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
    @PostMapping("/answer")
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
                    expertId = Long.parseLong(parts[1]);
                }
            }

            // 回答 写入数据�??
            // 写入同时查看问题状态是否为已回答，若不是则改为已回�??
            AnswerDTO answerDTO = new AnswerDTO(questionId, expertId, content);
            AnswerDTO savedDTO = answerService.createAnswer(answerDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            response.put("message", "回答提交成功");
            response.put("data", savedDTO);

            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "提交回答失败: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * GET /qa/experts
     * 获取专家列表
     * 显示在提问时希望得到哪位专家解答的下拉选择框中
     */
    @GetMapping("/experts")
    public ResponseEntity<Map<String, Object>> getExpertList() {
        try {
            // 使用 ExpertService 获取专家列表,它返�?? DTO,避免懒加载问�??
            List<com.farmporject.backend.expert.dto.ExpertDTO> experts = expertService.getAllExperts();

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (com.farmporject.backend.expert.dto.ExpertDTO expert : experts) {
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
     * GET /api/qa/test
     * 测试接口：直接查询数据库，返回原始数�??
     */
    // @GetMapping("/test")
    // public ResponseEntity<Map<String, Object>> testQuery() {
    // try {
    // System.out.println("=== 测试查询 ===");
    // List<Question> allQuestions = qaService.getAllQuestions();
    // System.out.println("数据库中的问题总数: " + allQuestions.size());

    // if (!allQuestions.isEmpty()) {
    // Question first = allQuestions.get(0);
    // String userIdStr = first.getUser() != null ?
    // String.valueOf(first.getUser().getId()) : "null";
    // System.out.println(
    // "第一个问�??: id=" + first.getId() + ", title=" + first.getTitle() + ", userId=" +
    // userIdStr);
    // }

    // Map<String, Object> response = new HashMap<>();
    // response.put("flag", true);
    // response.put("total", allQuestions.size());
    // response.put("message", "查询成功，共 " + allQuestions.size() + " 条记�??");
    // return ResponseEntity.ok(response);
    // } catch (Exception e) {
    // System.err.println("测试查询失败: " + e.getMessage());
    // e.printStackTrace();
    // Map<String, Object> response = new HashMap<>();
    // response.put("flag", false);
    // response.put("message", "查询失败: " + e.getMessage());
    // return ResponseEntity.status(500).body(response);
    // }
    // }

    /**
     * DELETE /api/qa/questions/{id}
     * 删除问题
     */
    @DeleteMapping("/questions/{id}")
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
            if (file == null || file.isEmpty())
                continue;
            String originalName = file.getOriginalFilename();
            Path path = Paths.get(uploadDir, originalName);
            file.transferTo(path.toFile());
            String url = "/qa_files/" + questionId + "/" + originalName;
            urls.add(url);
        }

        return urls;
    }
}
