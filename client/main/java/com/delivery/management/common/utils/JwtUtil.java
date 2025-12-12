package com.delivery.management.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.delivery.management.common.properties.JwtProperties;

/**
 * JWT 工具类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 生成 JWT Token
     * 
     * @param claims 载荷数据（包含 empId, branchId, username）
     * @return JWT Token
     */
    public String generateToken(Map<String, Object> claims) {
        try {
            SecretKey secretKey = getSecretKey();
            Date expiration = new Date(System.currentTimeMillis() + jwtProperties.getTtl());
            
            return Jwts.builder()
                    .claims(claims)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            log.error("JWT Token生成失败: {}", e.getMessage(), e);
            throw new RuntimeException("JWT Token生成失败", e);
        }
    }

    /**
     * 解析 JWT Token，获取 Claims
     * 
     * @param token JWT Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            SecretKey secretKey = getSecretKey();
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT Token 解析失败：{}", e.getMessage());
            throw new RuntimeException("JWT Token 解析失败", e);
        }
    }

    /**
     * 从 Token 中获取员工ID
     * 
     * @param token JWT Token
     * @return 员工ID
     */
    public Long getEmpIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object empId = claims.get("empId");
        if (empId instanceof Integer) {
            return ((Integer) empId).longValue();
        } else if (empId instanceof Long) {
            return (Long) empId;
        }
        return null;
    }

    /**
     * 从 Token 中获取分店ID
     * 
     * @param token JWT Token
     * @return 分店ID
     */
    public Long getBranchIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object branchId = claims.get("branchId");
        if (branchId instanceof Integer) {
            return ((Integer) branchId).longValue();
        } else if (branchId instanceof Long) {
            return (Long) branchId;
        }
        return null;
    }

    /**
     * 从 Token 中获取用户名
     * 
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 验证 Token 是否过期
     * 
     * @param token JWT Token
     * @return true-已过期，false-未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration == null || expiration.before(new Date());
        } catch (Exception e) {
            return true; // 如果解析失败，认为Token已过期
        }
    }

    /**
     * 获取密钥
     * 
     * @return SecretKey
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 从请求头中提取 Token
     * 
     * @param authorizationHeader Authorization 请求头
     * @return JWT Token（不包含 Bearer 前缀）
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(jwtProperties.getTokenPrefix())) {
            return authorizationHeader.substring(jwtProperties.getTokenPrefix().length());
        }
        return null;
    }
}

