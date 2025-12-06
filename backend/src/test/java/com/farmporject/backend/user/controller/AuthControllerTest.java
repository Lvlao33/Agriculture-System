package com.farmporject.backend.user.controller;

import com.farmporject.backend.common.api.ApiResponse;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setNickname("Alice");
        user.setAvatar("avatar.png");

        when(userService.validateLogin("alice", "pwd")).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<Map<String, Object>>> success = authController
                .login(Map.of("username", "alice", "password", "pwd"));

        assertEquals(HttpStatus.OK, success.getStatusCode());
        ApiResponse<Map<String, Object>> body = success.getBody();
        assertTrue(body.isSuccess());
        assertTrue(body.getData().get("token").toString().contains("tk_1_alice"));

        when(userService.validateLogin("bob", "pwd")).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse<Map<String, Object>>> fail = authController
                .login(Map.of("username", "bob", "password", "pwd"));

        assertEquals(HttpStatus.BAD_REQUEST, fail.getStatusCode());
        assertFalse(fail.getBody().isSuccess());
        assertEquals("用户名或密码错误", fail.getBody().getMessage());
    }

    @Test
    void register() {
        User user = new User();
        user.setUsername("alice");
        when(userService.register(eq("alice"), eq("pwd"), eq("Alice"), eq("avatar.png"))).thenReturn(user);

        ResponseEntity<ApiResponse<User>> response = authController
                .register(Map.of("username", "alice", "password", "pwd", "nickname", "Alice", "avatar", "avatar.png"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody().getData());
    }
}
