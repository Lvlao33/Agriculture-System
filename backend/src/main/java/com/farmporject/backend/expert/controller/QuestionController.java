package com.farmporject.backend.expert.controller;

import com.farmporject.backend.expert.model.Question;
import com.farmporject.backend.expert.service.QAService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兼容前端旧接口的问答控制器
 * 处理 /question 路径的请求
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QAService qaService;

    public QuestionController(QAService qaService) {
        this.qaService = qaService;
    }

    /**
     * GET /question/findAllQues/{pageNum}
     * 兼容前端旧接口：获取问答列表（分页）
     */
    @GetMapping("/findAllQues/{pageNum}")
    public ResponseEntity<Map<String, Object>> findAllQues(
            @PathVariable Integer pageNum,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            int pageSize = 10;
            List<Question> list = qaService.getAllQuestions();

            // 按更新时间降序排列
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

            // 转换为DTO格式
            List<Map<String, Object>> resultList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Question q : pagedList) {
                Map<String, Object> item = new HashMap<>();
                item.put("questionId", q.getId());
                item.put("title", q.getTitle() != null ? q.getTitle() : "");
                item.put("content", q.getContent() != null ? q.getContent() : "");
                
                // 处理用户信息
                try {
                    if (q.getUser() != null) {
                        item.put("questioner", q.getUser().getUsername());
                        item.put("userId", q.getUser().getId());
                    } else {
                        item.put("questioner", "匿名用户");
                        item.put("userId", null);
                    }
                } catch (Exception e) {
                    item.put("questioner", "匿名用户");
                    item.put("userId", null);
                }

                // 处理专家信息
                try {
                    if (q.getExpert() != null) {
                        item.put("expertName", q.getExpert().getName());
                        item.put("expertId", q.getExpert().getId());
                    } else {
                        item.put("expertName", "");
                        item.put("expertId", null);
                    }
                } catch (Exception e) {
                    item.put("expertName", "");
                    item.put("expertId", null);
                }

                item.put("status", q.getStatus() != null ? q.getStatus().name() : "PENDING");
                
                if (q.getCreateTime() != null) {
                    item.put("createTime", q.getCreateTime().format(formatter));
                } else {
                    item.put("createTime", null);
                }

                // 查询回答数量
                try {
                    int answerCount = qaService.getQuestionAnswersCount(q.getId());
                    item.put("answerCount", answerCount);
                } catch (Exception e) {
                    item.put("answerCount", 0);
                }

                resultList.add(item);
            }

            // 返回前端期望的格式：{ flag: true, data: { list: [...], total: ... } }
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", resultList);
            data.put("total", total);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取问答列表失败: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "获取问答列表失败: " + e.getMessage());
            Map<String, Object> data = new HashMap<>();
            data.put("list", new ArrayList<>());
            data.put("total", 0);
            response.put("data", data);

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /question/findPageQues/{keys}/{pageNum}
     * 兼容前端旧接口：根据关键词搜索问答列表（分页）
     */
    @GetMapping("/findPageQues/{keys}/{pageNum}")
    public ResponseEntity<Map<String, Object>> findPageQues(
            @PathVariable String keys,
            @PathVariable Integer pageNum,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            int pageSize = 10;
            List<Question> list;

            // 根据关键词搜索
            if (keys != null && !keys.trim().isEmpty()) {
                list = qaService.searchQuestionsByKeyword(keys.trim());
            } else {
                list = qaService.getAllQuestions();
            }

            // 按更新时间降序排列
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

            // 转换为DTO格式
            List<Map<String, Object>> resultList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Question q : pagedList) {
                Map<String, Object> item = new HashMap<>();
                item.put("questionId", q.getId());
                item.put("title", q.getTitle() != null ? q.getTitle() : "");
                item.put("content", q.getContent() != null ? q.getContent() : "");
                
                // 处理用户信息
                try {
                    if (q.getUser() != null) {
                        item.put("questioner", q.getUser().getUsername());
                        item.put("userId", q.getUser().getId());
                    } else {
                        item.put("questioner", "匿名用户");
                        item.put("userId", null);
                    }
                } catch (Exception e) {
                    item.put("questioner", "匿名用户");
                    item.put("userId", null);
                }

                // 处理专家信息
                try {
                    if (q.getExpert() != null) {
                        item.put("expertName", q.getExpert().getName());
                        item.put("expertId", q.getExpert().getId());
                    } else {
                        item.put("expertName", "");
                        item.put("expertId", null);
                    }
                } catch (Exception e) {
                    item.put("expertName", "");
                    item.put("expertId", null);
                }

                item.put("status", q.getStatus() != null ? q.getStatus().name() : "PENDING");
                
                if (q.getCreateTime() != null) {
                    item.put("createTime", q.getCreateTime().format(formatter));
                } else {
                    item.put("createTime", null);
                }

                // 查询回答数量
                try {
                    int answerCount = qaService.getQuestionAnswersCount(q.getId());
                    item.put("answerCount", answerCount);
                } catch (Exception e) {
                    item.put("answerCount", 0);
                }

                resultList.add(item);
            }

            // 返回前端期望的格式：{ flag: true, data: { list: [...], total: ... } }
            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", resultList);
            data.put("total", total);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("搜索问答列表失败: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("flag", false);
            response.put("message", "搜索问答列表失败: " + e.getMessage());
            Map<String, Object> data = new HashMap<>();
            data.put("list", new ArrayList<>());
            data.put("total", 0);
            response.put("data", data);

            return ResponseEntity.status(500).body(response);
        }
    }
}

