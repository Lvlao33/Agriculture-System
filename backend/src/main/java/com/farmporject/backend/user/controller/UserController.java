package com.farmporject.backend.user.controller;

import com.farmporject.backend.common.api.ApiResponse;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** 用户管理 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 找回密码 */
    @PostMapping("/forgetPassword")
    public ResponseEntity<ApiResponse<String>> forgetPassword(@RequestBody Map<String, String> body) {
        try {
            String username = body.getOrDefault("username", "");
            String newPassword = body.getOrDefault("newPassword", "");
            if (username.isEmpty() || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.fail("用户名和新密码不能为空"));
            }
            userService.forgetPassword(username, newPassword);
            return ResponseEntity.ok(ApiResponse.ok("密码重置成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    /** 根据用户名查询个人信息 */
    @GetMapping("/loginSelectByUsername")
    public ResponseEntity<ApiResponse<User>> loginSelectByUsername(@RequestParam(required = false) String username,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 优先使用请求参数中的username
            if (username != null && !username.isEmpty()) {
                return userService.findByUsername(username)
                        .<ResponseEntity<ApiResponse<User>>>map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                        .orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.fail("用户不存在")));
            }
            // 如果没有username参数，尝试从token中解析（简化实现）
            if (token != null && token.startsWith("tk_")) {
                // token格式: "tk_id_username"，提取username
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    String usernameFromToken = parts[2];
                    return userService.findByUsername(usernameFromToken)
                            .<ResponseEntity<ApiResponse<User>>>map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                            .orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.fail("用户不存在")));
                }
            }
            return ResponseEntity.badRequest().body(ApiResponse.fail("请提供用户名参数或有效的token"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    /** 修改个人信息 */
    @PostMapping("/loginUpdateByUsername")
    public ResponseEntity<ApiResponse<User>> loginUpdateByUsername(@RequestBody Map<String, String> body) {
        try {
            String username = body.getOrDefault("username", "");
            String nickname = body.getOrDefault("nickname", "");
            String avatar = body.getOrDefault("avatar", "");
            if (username.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.fail("用户名不能为空"));
            }
            User user = userService.updateUserInfo(username, nickname, avatar);
            return ResponseEntity.ok(ApiResponse.ok(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    /** 根据用户名查询用户 */
    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .<ResponseEntity<ApiResponse<User>>>map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                .orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.fail("用户不存在")));
    }

    /** 根据用户名删除用户 */
    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<String>> deleteUserByUsername(@PathVariable String username) {
        try {
            userService.deleteByUsername(username);
            return ResponseEntity.ok(ApiResponse.ok("用户删除成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    /** 根据用户名修改用户信息 */
    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<User>> updateUserByUsername(@PathVariable String username,
            @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(username, updatedUser);
            return ResponseEntity.ok(ApiResponse.ok(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    /** 分页查询所有用户 */
    @GetMapping("/search/{pageNum}")
    public ResponseEntity<ApiResponse<Page<User>>> selectAllUserPage(@PathVariable int pageNum) {
        try {
            int pageSize = 10; // 默认每页10条，可以根据需要调整
            Page<User> users = userService.findAllUsers(pageNum, pageSize);
            return ResponseEntity.ok(ApiResponse.ok(users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    /** 修改密码 */
    @PostMapping("/loginUpdatePassword")
    public ResponseEntity<ApiResponse<String>> updateUserPassword(@RequestBody Map<String, String> body) {
        try {
            String username = body.getOrDefault("username", "");
            String oldPassword = body.getOrDefault("oldPassword", "");
            String newPassword = body.getOrDefault("newPassword", "");
            if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.fail("用户名、原密码和新密码不能为空"));
            }
            userService.updatePassword(username, oldPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.ok("密码修改成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    /** 根据userID查询用户 （用于联合贷款邀请） */
    @GetMapping("/multi-Inviter/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("用户ID不能为空"));
        }
        return userService.findUserById(id)
                .<ResponseEntity<ApiResponse<User>>>map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                .orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.fail("用户不存在")));
    }
}
