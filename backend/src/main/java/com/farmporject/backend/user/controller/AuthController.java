package com.farmporject.backend.user.controller;

import com.farmporject.backend.common.api.ApiResponse;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** 用户-鉴权 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");
        return userService.validateLogin(username, password)
                .<ResponseEntity<ApiResponse<Map<String, Object>>>>map(user -> {
                    // 简化：token 用用户ID-用户名拼接（演示用）。生产应使用JWT。
                    String token = "tk_" + user.getId() + "_" + user.getUsername();
                    return ResponseEntity.ok(ApiResponse.ok(Map.of(
                            "token", token,
                            "user", Map.of(
                                    "id", user.getId(),
                                    "username", user.getUsername(),
                                    "nickname", user.getNickname(),
                                    "avatar", user.getAvatar(),
                                    "role", user.getRole() != null ? user.getRole() : "FARMER"
                            ),
                            "role", user.getRole() != null ? user.getRole() : "FARMER"
                    )));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.fail("用户名或密码错误")));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");
        String nickname = body.getOrDefault("nickname", "");
        String avatar = body.getOrDefault("avatar", "");
        String role = body.getOrDefault("role", "FARMER"); // 默认角色为 FARMER
        User user = userService.register(username, password, nickname, avatar, role);
        return ResponseEntity.status(201).body(ApiResponse.ok(user));
    }
}
