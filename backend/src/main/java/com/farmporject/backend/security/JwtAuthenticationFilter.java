package com.farmporject.backend.security;

import com.farmporject.backend.config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * JWT认证过滤器
 * 负责：
 * 1. 拦截所有HTTP请求
 * 2. 从Authorization header中提取JWT token
 * 3. 验证token的有效性
 * 4. 解析token获取用户信息并存入UserContext
 * 5. 对于无需认证的路由，直接放行
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfig jwtConfig;
    
    // 无需认证的路由黑名单
    private static final Set<String> PUBLIC_ROUTES = new HashSet<>();
    
    static {
        // 登录和注册接口无需token
        PUBLIC_ROUTES.add("/api/auth/login");
        PUBLIC_ROUTES.add("/api/auth/register");
        
        // 找回密码无需token
        PUBLIC_ROUTES.add("/api/user/forgetPassword");
        
        // 公开查询接口无需token
        PUBLIC_ROUTES.add("/api/home/data");
        PUBLIC_ROUTES.add("/api/experts");
        PUBLIC_ROUTES.add("/api/knowledge");
        PUBLIC_ROUTES.add("/api/finance/loan-products");
        PUBLIC_ROUTES.add("/api/trade/products");
        PUBLIC_ROUTES.add("/api/qa/questions");
        
        // 健康检查
        PUBLIC_ROUTES.add("/actuator/health");
        
        // 静态资源（允许 GET 请求访问，无需认证）
        PUBLIC_ROUTES.add("/img");
        PUBLIC_ROUTES.add("/order");
        PUBLIC_ROUTES.add("/kn");
        PUBLIC_ROUTES.add("/file");  // 静态资源访问路径
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean chainInvoked = false;

        try {
            // 获取请求路径和方法
            String requestPath = request.getRequestURI();
            String method = request.getMethod();

            // 检查是否是公开路由
            if (isPublicRoute(requestPath, method)) {
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            // 从Authorization header中获取token
            String authHeader = request.getHeader("Authorization");
            String token = jwtConfig.extractTokenFromHeader(authHeader);

            // 验证token
            if (token == null || token.isEmpty()) {
                System.err.println("请求缺少Authorization header: " + requestPath);
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            if (!jwtConfig.validateToken(token)) {
                System.err.println("Token验证失败: " + requestPath);
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            // 从token中提取用户信息
            Long userId = jwtConfig.getUserIdFromToken(token);
            String username = jwtConfig.getUsernameFromToken(token);
            String role = jwtConfig.getRoleFromToken(token);

            // 将用户信息存入ThreadLocal
            if (userId != null) {
                UserContext.setCurrentUser(userId, username, role);
                System.out.println("✓ Token验证成功 - 用户: " + username + ", 角色: " + role);
            }

            // 正常处理一次请求
            filterChain.doFilter(request, response);
            chainInvoked = true;

        } catch (Exception e) {
            System.err.println("认证过滤器异常: " + e.getMessage());
            try {
                if (!chainInvoked) {
                    filterChain.doFilter(request, response);
                }
            } catch (Exception ex) {
                System.err.println("认证过滤器二次处理异常: " + ex.getMessage());
            }
        } finally {
            // 请求结束时清理用户上下文
            UserContext.clear();
        }
    }
    
    /**
     * 检查是否是公开路由
     * @param path 请求路径
     * @param method HTTP 方法
     */
    private boolean isPublicRoute(String path, String method) {
        // 文件上传接口需要认证，不是公开路由
        if (path.startsWith("/file/upload")) {
            return false;
        }
        
        // 完全匹配
        if (PUBLIC_ROUTES.contains(path)) {
            // 对于 /file 路径，只允许 GET 请求（静态资源访问）
            if (path.startsWith("/file")) {
                return "GET".equalsIgnoreCase(method);
            }
            return true;
        }
        
        // 前缀匹配（用于带参数的API）
        for (String publicRoute : PUBLIC_ROUTES) {
            if (path.startsWith(publicRoute)) {
                // 对于 /file 路径，只允许 GET 请求（静态资源访问）
                if (path.startsWith("/file")) {
                    return "GET".equalsIgnoreCase(method);
                }
                return true;
            }
        }
        
        return false;
    }
}
