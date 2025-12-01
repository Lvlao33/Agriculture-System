package com.farmporject.backend.user.service;

import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class UserService {
    private static final String DEFAULT_ROLE = "FARMER";

    private final UserRepository userRepository;  //

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String rawPassword, String nickname, String avatar) {
        return register(username, rawPassword, nickname, avatar, null);
    }

    public User register(String username, String rawPassword, String nickname, String avatar, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashPassword(rawPassword));
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setRole(resolveRole(role));
        return userRepository.save(user);
    }

    public Optional<User> validateLogin(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPasswordHash().equals(hashPassword(rawPassword)));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUserInfo(String username, String nickname, String avatar) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }
        if (avatar != null && !avatar.isEmpty()) {
            user.setAvatar(avatar);
        }
        return userRepository.save(user);
    }

    public void updatePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getPasswordHash().equals(hashPassword(oldPassword))) {
            throw new IllegalArgumentException("Original password mismatch");
        }
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }

    public void forgetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }

    public void deleteByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }

    public User updateUser(String username, User updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (updatedUser.getNickname() != null) {
            user.setNickname(updatedUser.getNickname());
        }
        if (updatedUser.getAvatar() != null) {
            user.setAvatar(updatedUser.getAvatar());
        }
        return userRepository.save(user);
    }

    public Page<User> findAllUsers(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return userRepository.findAll(pageable);
    }

    public void ensureUserRole(String username, String targetRole) {
        userRepository.findByUsername(username).ifPresent(user -> {
            String resolvedRole = resolveRole(targetRole);
            if (!resolvedRole.equalsIgnoreCase(user.getRole())) {
                user.setRole(resolvedRole);
                userRepository.save(user);
            }
        });
    }

    public String hashPassword(String rawPassword) {
        return DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
    }

    private String resolveRole(String role) {
        if (role == null || role.isBlank()) {
            return DEFAULT_ROLE;
        }
        String upper = role.trim().toUpperCase();
        return switch (upper) {
            case "FARMER", "EXPERT", "BANK" -> upper;
            case "STAFF", "FINANCE", "BANKER" -> "BANK";
            default -> DEFAULT_ROLE;
        };
    }
}
