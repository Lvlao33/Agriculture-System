package com.farmporject.backend.expert.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KnowledgeControllerTest {

    private final KnowledgeController controller = new KnowledgeController();

    @Test
    void list() {
        ResponseEntity<?> response = controller.list(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(true, body.get("flag"));

        Map<?, ?> data = (Map<?, ?>) body.get("data");
        List<?> list = (List<?>) data.get("list");
        assertEquals(5, list.size());
        assertEquals(5, data.get("total"));
    }

    @Test
    void listAll() {
        ResponseEntity<?> response = controller.listAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        Map<?, ?> data = (Map<?, ?>) body.get("data");
        assertEquals(0, data.get("total"));
        assertTrue(((List<?>) data.get("list")).isEmpty());
    }

    @Test
    void create() {
        ResponseEntity<?> response = controller.create(Map.of("title", "new knowledge"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(true, body.get("flag"));
        assertEquals("knowledge created", body.get("message"));
    }
}