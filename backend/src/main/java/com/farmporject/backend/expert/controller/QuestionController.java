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
 * 问答控制器
 * 统一前缀: /api/question
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QAService qaService;

    public QuestionController(QAService qaService) {
        this.qaService = qaService;
    }

    // 补充缺失接口：解决 ExpertQuestion.vue 的报错
    @GetMapping("/selectByKind/{kind}")
    public ResponseEntity<Map<String, Object>> selectByKind(@PathVariable String kind) {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Question> list = qaService.getAllQuestions();
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Question q : list) {
                Map<String, Object> item = new HashMap<>();
                item.put("questionId", q.getId());
                item.put("title", q.getTitle());
                item.put("content", q.getContent());
                item.put("status", q.getStatus());
                resultList.add(item);
            }
            resp.put("flag", true);
            resp.put("data", resultList);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "查询失败");
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // 删除接口
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            qaService.deleteQuestion(id);
            resp.put("flag", true);
            resp.put("message", "删除成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("flag", false);
            resp.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // 分页查询接口
    @GetMapping("/findAllQues/{pageNum}")
    public ResponseEntity<Map<String, Object>> findAllQues(@PathVariable Integer pageNum) {
        try {
            int pageSize = 10;
            List<Question> list = qaService.getAllQuestions();
            int total = list.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Question> pagedList = (start < total) ? list.subList(start, end) : new ArrayList<>();

            List<Map<String, Object>> resultList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Question q : pagedList) {
                Map<String, Object> item = new HashMap<>();
                item.put("questionId", q.getId());
                item.put("title", q.getTitle());
                item.put("content", q.getContent());
                if (q.getUser() != null) {
                    item.put("questioner", q.getUser().getUsername());
                    item.put("userId", q.getUser().getId());
                } else {
                    item.put("questioner", "匿名");
                }
                if (q.getExpert() != null) {
                    item.put("expertName", q.getExpert().getName());
                }
                item.put("status", q.getStatus());
                if (q.getCreateTime() != null) {
                    item.put("createTime", q.getCreateTime().format(formatter));
                }
                try {
                    item.put("answerCount", qaService.getQuestionAnswersCount(q.getId()));
                } catch (Exception e) {
                    item.put("answerCount", 0);
                }
                resultList.add(item);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("flag", true);
            Map<String, Object> data = new HashMap<>();
            data.put("list", resultList);
            data.put("total", total);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}