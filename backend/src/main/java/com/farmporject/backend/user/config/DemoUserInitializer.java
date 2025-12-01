package com.farmporject.backend.user.config;

import com.farmporject.backend.user.model.User;
import com.farmporject.backend.user.repository.UserRepository;
import com.farmporject.backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seed demo accounts for farmer/expert/bank roles.
 * Disable via app.demo-users.enabled=false when not needed.
 */
@Component
public class DemoUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.demo-users.enabled:true}")
    private boolean enabled;

    public DemoUserInitializer(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        createOrUpdate("farmer01", "123456", "FARMER", "Farmer Demo");
        createOrUpdate("expert01", "123456", "EXPERT", "Expert Demo");
        createOrUpdate("bank01", "123456", "BANK", "Bank Demo");
    }

    private void createOrUpdate(String username, String password, String role, String nickname) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            if (roleChanged(user, role)) {
                user.setRole(role);
                userRepository.save(user);
            }
        }, () -> {
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(userService.hashPassword(password));
            user.setNickname(nickname);
            user.setRole(role);
            userRepository.save(user);
        });
    }

    private boolean roleChanged(User user, String role) {
        if (user.getRole() == null) {
            return true;
        }
        return !user.getRole().equalsIgnoreCase(role);
    }
}

