package com.farmporject.backend.expert.controller;

import com.farmporject.backend.expert.model.Knowledge;
import com.farmporject.backend.expert.service.CommentService;
import com.farmporject.backend.expert.service.KnowledgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeControllerTest {

    @Mock
    private KnowledgeService knowledgeService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private KnowledgeController controller;

    @Test
    void list() {
        // 准备测试数据
        List<Knowledge> knowledgeList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Knowledge knowledge = new Knowledge();
            knowledge.setKnowledgeId((long) i);
            knowledge.setTitle("Test Knowledge " + i);
            knowledgeList.add(knowledge);
        }
        Page<Knowledge> knowledgePage = new PageImpl<>(knowledgeList, PageRequest.of(0, 10), 5);

        when(knowledgeService.getPublishedKnowledge(any(Pageable.class))).thenReturn(knowledgePage);

        ResponseEntity<?> response = controller.list(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(true, body.get("flag"));

        Map<?, ?> data = (Map<?, ?>) body.get("data");
        List<?> list = (List<?>) data.get("list");
        assertEquals(5, list.size());
        assertEquals(5L, data.get("total"));
    }

    @Test
    void selectByUsername() {
        // 准备空数据
        Page<Knowledge> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 100), 0);

        when(knowledgeService.getPublishedKnowledge(any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<?> response = controller.selectByUsername();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        Map<?, ?> data = (Map<?, ?>) body.get("data");
        assertEquals(0L, data.get("total"));
        assertTrue(((List<?>) data.get("list")).isEmpty());
    }

    @Test
    void create() {
        Knowledge knowledge = new Knowledge();
        knowledge.setTitle("new knowledge");
        knowledge.setContent("test content");
        knowledge.setCreateTime(LocalDateTime.now());
        knowledge.setUpdateTime(LocalDateTime.now());

        Knowledge savedKnowledge = new Knowledge();
        savedKnowledge.setKnowledgeId(1L);
        savedKnowledge.setTitle("new knowledge");

        when(knowledgeService.createKnowledge(any(Knowledge.class))).thenReturn(savedKnowledge);

        ResponseEntity<?> response = controller.create(knowledge);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(true, body.get("flag"));
        assertEquals("知识创建成功", body.get("message"));
    }
}