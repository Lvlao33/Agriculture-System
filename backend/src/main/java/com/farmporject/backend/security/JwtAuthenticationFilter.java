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
 * * 修改说明：移除了混合权限接口（如/api/knowledge）的白名单配置，
 * 确保后端能解析Token并识别专家/商家身份。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfig jwtConfig;
    
    // 无需认证的路由黑名单
    private static final Set<String> PUBLIC_ROUTES = new HashSet<>();
    
    static {
        // ============================================================
        // 1. 纯公开接口（绝对不需要用户信息）
        // ============================================================
        PUBLIC_ROUTES.add("/api/auth/login");
        PUBLIC_ROUTES.add("/api/auth/register");
        PUBLIC_ROUTES.add("/api/user/forgetPassword");
        
        // ============================================================
        // 2. 静态资源和监控
        // ============================================================
        PUBLIC_ROUTES.add("/actuator/health");
        PUBLIC_ROUTES.add("/img");
        PUBLIC_ROUTES.add("/file");
        // 如果你的静态资源路径不同，请在这里补充，例如 /uploads
        
        // ============================================================
        // 3. 业务接口白名单配置（已修正）
        // ============================================================
        /* * 注意：以下接口已被注释掉！
         * 原因：这些接口虽然允许游客访问（GET），但也包含专家/商家的操作（POST/PUT/DELETE）。
         * 如果加入白名单，过滤器会直接跳过 Token 解析，导致 Controller 拿不到 UserContext，
         * 从而导致专家发布知识时报 401 错误，或者商家看不到自己的商品。
         * * 即使注释掉，下方的 doFilterInternal 逻辑允许没有 Token 的请求通过（视为游客），
         * 所以不会影响普通用户的浏览功能。
         */
        
        // PUBLIC_ROUTES.add("/api/knowledge"); 
        // PUBLIC_ROUTES.add("/api/expert");
        // PUBLIC_ROUTES.add("/api/qa");
        // PUBLIC_ROUTES.add("/api/appointments");
        // PUBLIC_ROUTES.add("/api/expert/dashboard");
        // PUBLIC_ROUTES.add("/api/question");
        
        // 商品和金融接口同理，如果需要商家身份，请务必注释掉
        // PUBLIC_ROUTES.add("/api/finance/loan-products");
        // PUBLIC_ROUTES.add("/api/trade/products");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean chainInvoked = false;

        try {
            // 获取请求路径
            String requestPath = request.getRequestURI();

            // 检查是否是强制公开路由（仅跳过纯登录注册接口）
            if (isPublicRoute(requestPath)) {
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            // 从Authorization header中获取token
            String authHeader = request.getHeader("Authorization");
            String token = jwtConfig.extractTokenFromHeader(authHeader);

            // ============================================================
            // 核心逻辑修改：兼容 游客模式 和 登录模式
            // ============================================================

            // 情况A：没有Token -> 放行（视为游客），UserContext 为空
            // 这样 KnowledgeController 的 list 接口可以正常工作（游客模式）
            // 但 create 接口会因为 UserContext 为空而在 Controller 层拦截，这是正确的
            if (token == null || token.isEmpty()) {
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            // 情况B：有Token但无效 -> 放行（视为游客），UserContext 为空
            // 这样如果前端传了过期 Token，用户只是变成了“游客”，而不是直接报错页面无法加载
            // (根据实际需求，这里也可以选择直接返回 401 强制前端重新登录)
            if (!jwtConfig.validateToken(token)) {
                System.err.println("Token验证失败或已过期: " + requestPath);
                // 选择放行，视为无登录状态
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            // 情况C：Token有效 -> 解析并设置 UserContext
            // 只有这里执行了，Controller 里的 UserContext.getCurrentUserId() 才有值！
            Long userId = jwtConfig.getUserIdFromToken(token);
            String username = jwtConfig.getUsernameFromToken(token);
            String role = jwtConfig.getRoleFromToken(token);

            if (userId != null) {
                UserContext.setCurrentUser(userId, username, role);
                // System.out.println("用户认证成功: " + username + " (" + role + ")"); // 调试用
            }

            // 继续处理请求
            filterChain.doFilter(request, response);
            chainInvoked = true;

        } catch (Exception e) {
            System.err.println("认证过滤器异常: " + e.getMessage());
            try {
                // 如果过滤器出错，尝试让请求继续，避免整个页面崩溃
                if (!chainInvoked) {
                    filterChain.doFilter(request, response);
                }
            } catch (Exception ex) {
                System.err.println("认证过滤器二次处理异常: " + ex.getMessage());
            }
        } finally {
            // 请求结束时清理用户上下文，防止线程污染
            UserContext.clear();
        }
    }
    
    /**
     * 检查是否是公开路由
     */
    private boolean isPublicRoute(String path) {
        // 完全匹配
        if (PUBLIC_ROUTES.contains(path)) {
            return true;
        }
        
        // 前缀匹配（用于带参数的API）
        for (String publicRoute : PUBLIC_ROUTES) {
            if (path.startsWith(publicRoute)) {
                return true;
            }
        }
        
        return false;
    }
}