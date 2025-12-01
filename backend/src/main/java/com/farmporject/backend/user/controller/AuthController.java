package com.farmporject.backend.user.controller;

import com.farmporject.backend.common.api.ApiResponse;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/** User authentication endpoints */
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
                    // Simplified demo token concatenates userId and username. Use JWT in production.
                    String token = "tk_" + user.getId() + "_" + user.getUsername();
                    Map<String, Object> userPayload = buildUserPayload(user);
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("role", userPayload.get("roleKey"));
                    response.put("roleName", userPayload.get("roleName"));
                    response.put("user", userPayload);
                    return ResponseEntity.ok(ApiResponse.ok(response));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.fail("Invalid username or password")));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");
        String nickname = body.getOrDefault("nickname", "");
        String avatar = body.getOrDefault("avatar", "");
        String role = body.getOrDefault("role", "");
        User user = userService.register(username, password, nickname, avatar, role);
        return ResponseEntity.status(201).body(ApiResponse.ok(user));
    }

    private Map<String, Object> buildUserPayload(User user) {
        Map<String, Object> payload = new HashMap<>();
        String normalizedRole = normalizeRole(user.getRole());
        payload.put("id", user.getId());
        payload.put("username", user.getUsername());
        payload.put("nickname", user.getNickname());
        payload.put("avatar", user.getAvatar());
        payload.put("role", user.getRole());
        payload.put("roleKey", normalizedRole);
        payload.put("roleName", roleDisplayName(normalizedRole));
        return payload;
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return "farmer";
        }
        String upper = rawRole.trim().toUpperCase();
        return switch (upper) {
            case "FARMER" -> "farmer";
            case "EXPERT" -> "expert";
            case "BANK", "STAFF", "FINANCE", "BANKER" -> "bank";
            default -> "farmer";
        };
    }

    private String roleDisplayName(String normalizedRole) {
        return switch (normalizedRole) {
            case "expert" -> "Expert";
            case "bank" -> "Bank";
            default -> "Farmer";
        };
    }
}
