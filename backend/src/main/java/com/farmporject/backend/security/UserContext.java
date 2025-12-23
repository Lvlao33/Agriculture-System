package com.farmporject.backend.security;

/**
 * 用户上下文 - 使用ThreadLocal存储当前请求的用户信息
 * 业务代码可通过此类获取当前用户的身份信息，无需传递参数
 * 
 * 使用示例：
 * Long userId = UserContext.getCurrentUserId();
 * String username = UserContext.getCurrentUsername();
 * String role = UserContext.getCurrentRole();
 */
public class UserContext {
    
    private static final ThreadLocal<Long> userIdContext = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameContext = new ThreadLocal<>();
    private static final ThreadLocal<String> roleContext = new ThreadLocal<>();
    
    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(Long userId, String username, String role) {
        if (userId != null) {
            userIdContext.set(userId);
        }
        if (username != null) {
            usernameContext.set(username);
        }
        if (role != null) {
            roleContext.set(role);
        }
    }
    
    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return userIdContext.get();
    }
    
    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        return usernameContext.get();
    }
    
    /**
     * 获取当前用户角色
     */
    public static String getCurrentRole() {
        return roleContext.get();
    }
    
    /**
     * 检查用户是否已认证
     */
    public static boolean isAuthenticated() {
        return userIdContext.get() != null;
    }
    
    /**
     * 清理当前用户信息（请求结束时调用）
     */
    public static void clear() {
        userIdContext.remove();
        usernameContext.remove();
        roleContext.remove();
    }
    
    /**
     * 检查用户是否具有指定角色
     */
    public static boolean hasRole(String role) {
        String currentRole = roleContext.get();
        return currentRole != null && currentRole.equalsIgnoreCase(role);
    }
}
