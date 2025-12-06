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

    private final UserRepository userRepository; //

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String rawPassword, String nickname, String avatar) {
        return register(username, rawPassword, nickname, avatar, DEFAULT_ROLE);
    }

    public User register(String username, String rawPassword, String nickname, String avatar, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 验证角色是否有效
        String validRole = validateRole(role);
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashPassword(rawPassword));
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setRole(validRole);
        return userRepository.save(user);
    }

    private String validateRole(String role) {
        if (role == null || role.isEmpty()) {
            return DEFAULT_ROLE;
        }
        // 支持大小写，统一转换为大写
        String upperRole = role.toUpperCase();
        if (upperRole.equals("FARMER") || upperRole.equals("EXPERT") || upperRole.equals("BANK")) {
            return upperRole;
        }
        // 如果角色不合法，返回默认角色
        return DEFAULT_ROLE;
    }

    public Optional<User> validateLogin(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPasswordHash().equals(hashPassword(rawPassword)));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        // 初始化懒加载字段，避免 LazyInitializationException
        userOpt.ifPresent(user -> {
            try {
                // 初始化 expert 板块的懒加载集合
                if (user.getAppointments() != null) {
                    user.getAppointments().size();
                }
                if (user.getQuestions() != null) {
                    user.getQuestions().size();
                }
                // 初始化 finance 板块的懒加载集合
                if (user.getLoanUserStatuses() != null) {
                    user.getLoanUserStatuses().size();
                }
                if (user.getLoanrRecords() != null) {
                    user.getLoanrRecords().size();
                }
                if (user.getLoanProducts() != null) {
                    user.getLoanProducts().size();
                }
                if (user.getAssignedLoans() != null) {
                    user.getAssignedLoans().size();
                }
            } catch (Exception e) {
                // 忽略懒加载失败的异常
            }
        });
        return userOpt;
    }

    public User updateUserInfo(String username, String nickname, String avatar) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
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
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (!user.getPasswordHash().equals(hashPassword(oldPassword))) {
            throw new IllegalArgumentException("原密码错误");
        }
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }

    public void forgetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);
    }

    public void deleteByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        userRepository.delete(user);
    }

    public User updateUser(String username, User updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (updatedUser.getNickname() != null) {
            user.setNickname(updatedUser.getNickname());
        }
        if (updatedUser.getAvatar() != null) {
            user.setAvatar(updatedUser.getAvatar());
        }
        return userRepository.save(user);
    }

    public Page<User> findAllUsers(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize); // 前端从1开始，后端从0开始
        return userRepository.findAll(pageable);
    }

    private String hashPassword(String rawPassword) {
        return DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
    }
}
