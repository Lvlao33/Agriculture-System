package com.farmporject.backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token生成和验证工具类
 * 负责：
 * - 生成JWT token（包含userId, username, role等信息）
 * - 验证token的有效性（签名、过期时间等）
 * - 解析token获取用户信息
 */
@Component
public class JwtConfig {
    
    // JWT密钥（建议放在application.properties中，生产环境单独配置）
    @Value("${jwt.secret:AgricultureSystemJwtSecret2025AgricultureSystemJwtSecret2025}")
    private String secret;
    
    // Token有效期（毫秒）：13小时
    @Value("${jwt.expiration:46800000}")
    private Long expiration;
    
    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色（farmer, expert, bank）
     * @return JWT Token字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * 验证Token是否有效
     * @param token JWT Token
     * @return true 有效，false 无效
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            System.err.println("JWT签名验证失败: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.err.println("JWT格式错误: " + e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT已过期: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT类型不支持: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("JWT声明为空: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 从Token中获取所有claims
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            System.err.println("解析JWT失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        if (claims == null) {
            return null;
        }
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        } else if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        if (claims == null) {
            return null;
        }
        return (String) claims.get("username");
    }
    
    /**
     * 从Token中获取用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        if (claims == null) {
            return null;
        }
        return (String) claims.get("role");
    }
    
    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            if (claims == null) {
                return true;
            }
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * 从Authorization header中提取Token
     * 格式: "Bearer <token>"
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
