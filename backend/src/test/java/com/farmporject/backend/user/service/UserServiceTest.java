package com.farmporject.backend.user.service;

import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setPasswordHash("oldHash");
        user.setNickname("Alice");
        user.setAvatar("avatar");
    }

    @Test
    void register() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.register("alice", "pwd", "Alice", "avatar");

        assertEquals("alice", created.getUsername());
        assertNotEquals("pwd", created.getPasswordHash());
        assertEquals("FARMER", created.getRole());

        when(userRepository.existsByUsername("alice")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.register("alice", "pwd", "", ""));
    }

    @Test
    void validateLogin() {
        user.setPasswordHash(UserServiceTestHelper.md5("pwd"));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertTrue(userService.validateLogin("alice", "pwd").isPresent());
        assertTrue(userService.validateLogin("alice", "wrong").isEmpty());
    }

    @Test
    void findByUsername() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        assertTrue(userService.findByUsername("alice").isPresent());
        verify(userRepository).findByUsername("alice");
    }

    @Test
    void updateUserInfo() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateUserInfo("alice", "Alice2", "avatar2");
        assertEquals("Alice2", updated.getNickname());
        assertEquals("avatar2", updated.getAvatar());
    }

    @Test
    void updatePassword() {
        user.setPasswordHash(UserServiceTestHelper.md5("old"));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        userService.updatePassword("alice", "old", "new");
        verify(userRepository).save(user);

        assertThrows(IllegalArgumentException.class, () -> userService.updatePassword("alice", "bad", "new"));
    }

    @Test
    void forgetPassword() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        userService.forgetPassword("alice", "new");

        verify(userRepository).save(user);
    }

    @Test
    void deleteByUsername() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        userService.deleteByUsername("alice");

        verify(userRepository).delete(user);
    }

    @Test
    void updateUser() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = new User();
        updated.setNickname("new");
        updated.setAvatar("newAvatar");

        User result = userService.updateUser("alice", updated);
        assertEquals("new", result.getNickname());
        assertEquals("newAvatar", result.getAvatar());
    }

    @Test
    void findAllUsers() {
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(user)));

        Page<User> page = userService.findAllUsers(1, 10);

        assertEquals(1, page.getTotalElements());
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(userRepository).findAll(captor.capture());
        assertEquals(0, captor.getValue().getPageNumber());
        assertEquals(10, captor.getValue().getPageSize());
    }

    /**
     * Helper to reuse hashing expectation.
     */
    private static class UserServiceTestHelper {
        static String md5(String raw) {
            return org.springframework.util.DigestUtils.md5DigestAsHex(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}
