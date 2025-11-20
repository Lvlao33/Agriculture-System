package com.farmporject.backend.user.controller;

import com.farmporject.backend.common.api.ApiResponse;
import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setNickname("Alice");
        return user;
    }

    @Test
    void forgetPassword() {
        doNothing().when(userService).forgetPassword("alice", "new");

        ResponseEntity<ApiResponse<String>> response = userController
                .forgetPassword(Map.of("username", "alice", "newPassword", "new"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("密码重置成功", response.getBody().getData());

        ResponseEntity<ApiResponse<String>> badRequest = userController
                .forgetPassword(Map.of("username", "", "newPassword", ""));
        assertEquals(HttpStatus.BAD_REQUEST, badRequest.getStatusCode());
    }

    @Test
    void loginSelectByUsername() {
        when(userService.findByUsername("alice")).thenReturn(Optional.of(sampleUser()));

        ResponseEntity<ApiResponse<User>> response = userController.loginSelectByUsername("alice", null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("alice", response.getBody().getData().getUsername());

        User bob = sampleUser();
        bob.setUsername("bob");
        when(userService.findByUsername("bob")).thenReturn(Optional.of(bob));
        ResponseEntity<ApiResponse<User>> byToken = userController.loginSelectByUsername(null, "tk_2_bob");
        assertEquals(HttpStatus.OK, byToken.getStatusCode());

        ResponseEntity<ApiResponse<User>> error = userController.loginSelectByUsername(null, null);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void loginUpdateByUsername() {
        User user = sampleUser();
        when(userService.updateUserInfo("alice", "Alice2", "avatar.png")).thenReturn(user);

        ResponseEntity<ApiResponse<User>> response = userController
                .loginUpdateByUsername(Map.of("username", "alice", "nickname", "Alice2", "avatar", "avatar.png"));

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<ApiResponse<User>> bad = userController.loginUpdateByUsername(Map.of());
        assertEquals(HttpStatus.BAD_REQUEST, bad.getStatusCode());
    }

    @Test
    void getUserByUsername() {
        when(userService.findByUsername("alice")).thenReturn(Optional.of(sampleUser()));
        ResponseEntity<ApiResponse<User>> ok = userController.getUserByUsername("alice");
        assertEquals(HttpStatus.OK, ok.getStatusCode());

        when(userService.findByUsername("missing")).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse<User>> missing = userController.getUserByUsername("missing");
        assertEquals(HttpStatus.BAD_REQUEST, missing.getStatusCode());
    }

    @Test
    void deleteUserByUsername() {
        doNothing().when(userService).deleteByUsername("alice");

        ResponseEntity<ApiResponse<String>> ok = userController.deleteUserByUsername("alice");
        assertEquals(HttpStatus.OK, ok.getStatusCode());

        doThrow(new IllegalArgumentException("不存在")).when(userService).deleteByUsername("bob");
        ResponseEntity<ApiResponse<String>> bad = userController.deleteUserByUsername("bob");
        assertEquals(HttpStatus.BAD_REQUEST, bad.getStatusCode());
    }

    @Test
    void updateUserByUsername() {
        User updated = sampleUser();
        when(userService.updateUser(eq("alice"), any(User.class))).thenReturn(updated);

        ResponseEntity<ApiResponse<User>> response = userController.updateUserByUsername("alice", new User());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        when(userService.updateUser(eq("bob"), any(User.class))).thenThrow(new IllegalArgumentException("error"));
        ResponseEntity<ApiResponse<User>> bad = userController.updateUserByUsername("bob", new User());
        assertEquals(HttpStatus.BAD_REQUEST, bad.getStatusCode());
    }

    @Test
    void selectAllUserPage() {
        Page<User> page = new PageImpl<>(List.of(sampleUser()));
        when(userService.findAllUsers(1, 10)).thenReturn(page);

        ResponseEntity<ApiResponse<Page<User>>> response = userController.selectAllUserPage(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().getTotalElements());
    }

    @Test
    void updateUserPassword() {
        doNothing().when(userService).updatePassword("alice", "old", "new");

        ResponseEntity<ApiResponse<String>> ok = userController
                .updateUserPassword(Map.of("username", "alice", "oldPassword", "old", "newPassword", "new"));
        assertEquals(HttpStatus.OK, ok.getStatusCode());

        ResponseEntity<ApiResponse<String>> bad = userController
                .updateUserPassword(Map.of("username", "", "oldPassword", "", "newPassword", ""));
        assertEquals(HttpStatus.BAD_REQUEST, bad.getStatusCode());
    }
}
