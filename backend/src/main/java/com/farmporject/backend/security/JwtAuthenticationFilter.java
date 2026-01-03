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
 * JWT璁よ瘉杩囨护鍣�
 * 璐熻矗锛�
 * 1. 鎷︽埅鎵€鏈塇TTP璇锋眰
 * 2. 浠嶢uthorization header涓彁鍙朖WT token
 * 3. 楠岃瘉token鐨勬湁鏁堟€�
 * 4. 瑙ｆ瀽token鑾峰彇鐢ㄦ埛淇℃伅骞跺瓨鍏serContext
 * 5. 瀵逛簬鏃犻渶璁よ瘉鐨勮矾鐢憋紝鐩存帴鏀捐
 * * 淇敼璇存槑锛氱Щ闄や簡娣峰悎鏉冮檺鎺ュ彛锛堝/api/knowledge锛夌殑鐧藉悕鍗曢厤缃紝
 * 纭繚鍚庣鑳借В鏋怲oken骞惰瘑鍒笓瀹�/鍟嗗韬唤銆�
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfig jwtConfig;
    
    // 鏃犻渶璁よ瘉鐨勮矾鐢遍粦鍚嶅崟
    private static final Set<String> PUBLIC_ROUTES = new HashSet<>();
    
    static {
        // ============================================================
        // 1. 绾叕寮€鎺ュ彛锛堢粷瀵逛笉闇€瑕佺敤鎴蜂俊鎭級
        // ============================================================
        PUBLIC_ROUTES.add("/api/auth/login");
        PUBLIC_ROUTES.add("/api/auth/register");
        PUBLIC_ROUTES.add("/api/user/forgetPassword");
        
        // 鍏紑鏌ヨ鎺ュ彛鏃犻渶token
        PUBLIC_ROUTES.add("/api/experts");
        PUBLIC_ROUTES.add("/api/knowledge");
        PUBLIC_ROUTES.add("/api/finance/loan-products");
        PUBLIC_ROUTES.add("/api/trade/products");
        PUBLIC_ROUTES.add("/api/qa/questions");
        
        // 鍋ュ悍妫€鏌�
        PUBLIC_ROUTES.add("/actuator/health");
        
        // 闈欐€佽祫婧�
        PUBLIC_ROUTES.add("/img");
        PUBLIC_ROUTES.add("/order");
        PUBLIC_ROUTES.add("/kn");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean chainInvoked = false;

        try {
            // 鑾峰彇璇锋眰璺緞鍜屾柟娉�
            String requestPath = request.getRequestURI();
            String method = request.getMethod();

            // 妫€鏌ユ槸鍚︽槸寮哄埗鍏紑璺敱锛堜粎璺宠繃绾櫥褰曟敞鍐屾帴鍙ｏ級
            if (isPublicRoute(requestPath, method)) {
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            // 浠嶢uthorization header涓幏鍙杢oken
            String authHeader = request.getHeader("Authorization");
            String token = jwtConfig.extractTokenFromHeader(authHeader);

            // ============================================================
            // 鏍稿績閫昏緫淇敼锛氬吋瀹� 娓稿妯″紡 鍜� 鐧诲綍妯″紡
            // ============================================================

            // 鎯呭喌A锛氭病鏈塗oken -> 鏀捐锛堣涓烘父瀹級锛孶serContext 涓虹┖
            // 杩欐牱 KnowledgeController 鐨� list 鎺ュ彛鍙互姝ｅ父宸ヤ綔锛堟父瀹㈡ā寮忥級
            // 浣� create 鎺ュ彛浼氬洜涓� UserContext 涓虹┖鑰屽湪 Controller 灞傛嫤鎴紝杩欐槸姝ｇ‘鐨�
            if (token == null || token.isEmpty()) {
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            // 鎯呭喌B锛氭湁Token浣嗘棤鏁� -> 鏀捐锛堣涓烘父瀹級锛孶serContext 涓虹┖
            // 杩欐牱濡傛灉鍓嶇浼犱簡杩囨湡 Token锛岀敤鎴峰彧鏄彉鎴愪簡鈥滄父瀹⑩€濓紝鑰屼笉鏄洿鎺ユ姤閿欓〉闈㈡棤娉曞姞杞�
            // (鏍规嵁瀹為檯闇€姹傦紝杩欓噷涔熷彲浠ラ€夋嫨鐩存帴杩斿洖 401 寮哄埗鍓嶇閲嶆柊鐧诲綍)
            if (!jwtConfig.validateToken(token)) {
                System.err.println("Token楠岃瘉澶辫触鎴栧凡杩囨湡: " + requestPath);
                // 閫夋嫨鏀捐锛岃涓烘棤鐧诲綍鐘舵€�
                filterChain.doFilter(request, response);
                chainInvoked = true;
                return;
            }

            // 鎯呭喌C锛歍oken鏈夋晥 -> 瑙ｆ瀽骞惰缃� UserContext
            // 鍙湁杩欓噷鎵ц浜嗭紝Controller 閲岀殑 UserContext.getCurrentUserId() 鎵嶆湁鍊硷紒
            Long userId = jwtConfig.getUserIdFromToken(token);
            String username = jwtConfig.getUsernameFromToken(token);
            String role = jwtConfig.getRoleFromToken(token);

            if (userId != null) {
                UserContext.setCurrentUser(userId, username, role);
                // System.out.println("鐢ㄦ埛璁よ瘉鎴愬姛: " + username + " (" + role + ")"); // 璋冭瘯鐢�
            }

            // 缁х画澶勭悊璇锋眰
            filterChain.doFilter(request, response);
            chainInvoked = true;

        } catch (Exception e) {
            System.err.println("璁よ瘉杩囨护鍣ㄥ紓甯�: " + e.getMessage());
            try {
                // 濡傛灉杩囨护鍣ㄥ嚭閿欙紝灏濊瘯璁╄姹傜户缁紝閬垮厤鏁翠釜椤甸潰宕╂簝
                if (!chainInvoked) {
                    filterChain.doFilter(request, response);
                }
            } catch (Exception ex) {
                System.err.println("璁よ瘉杩囨护鍣ㄤ簩娆″鐞嗗紓甯�: " + ex.getMessage());
            }
        } finally {
            // 璇锋眰缁撴潫鏃舵竻鐞嗙敤鎴蜂笂涓嬫枃锛岄槻姝㈢嚎绋嬫薄鏌�
            UserContext.clear();
        }
    }
    
    /**
     * 妫€鏌ユ槸鍚︽槸鍏紑璺敱
     * @param path 璇锋眰璺緞
     * @param method HTTP 鏂规硶
     */
    private boolean isPublicRoute(String path, String method) {
        // 鏂囦欢涓婁紶鎺ュ彛闇€瑕佽璇侊紝涓嶆槸鍏紑璺敱
        if (path.startsWith("/file/upload")) {
            return false;
        }
        
        // 瀹屽叏鍖归厤
        if (PUBLIC_ROUTES.contains(path)) {
            // 瀵逛簬 /file 璺緞锛屽彧鍏佽 GET 璇锋眰锛堥潤鎬佽祫婧愯闂級
            if (path.startsWith("/file")) {
                return "GET".equalsIgnoreCase(method);
            }
            return true;
        }
        
        // 鍓嶇紑鍖归厤锛堢敤浜庡甫鍙傛暟鐨凙PI锛�
        for (String publicRoute : PUBLIC_ROUTES) {
            if (path.startsWith(publicRoute)) {
                // 瀵逛簬 /file 璺緞锛屽彧鍏佽 GET 璇锋眰锛堥潤鎬佽祫婧愯闂級
                if (path.startsWith("/file")) {
                    return "GET".equalsIgnoreCase(method);
                }
                return true;
            }
        }
        
        return false;
    }
}