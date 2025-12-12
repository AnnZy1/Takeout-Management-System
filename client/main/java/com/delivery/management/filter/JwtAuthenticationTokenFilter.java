package com.delivery.management.filter;

import com.delivery.management.common.context.BaseContext;
import com.delivery.management.common.properties.JwtProperties;
import com.delivery.management.common.result.Result;
import com.delivery.management.common.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 不需要Token验证的路径
     */
    private static final String[] WHITE_LIST = {
            "/admin/auth/login",
            "/admin/auth/logout",
            "/admin/auth/force-unlock/",
            "/doc.html",
            "/webjars/",
            "/swagger-resources/",
            "/v3/api-docs"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT Filter processing request: {}", request.getRequestURI());
        
        // 检查是否为白名单路径
        if (isWhiteList(request.getRequestURI())) {
            log.info("Request path is in whitelist, skipping JWT validation");
            filterChain.doFilter(request, response);
            return;
        }

        // 从请求头中获取Token
        String token = request.getHeader(jwtProperties.getTokenHeaderName());
        log.info("Request token header: {}", token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : null);
        
        // 重置BaseContext，确保每次请求都是干净的
        BaseContext.removeCurrentId();
        
        // 检查是否有Token
        if (!StringUtils.hasText(token)) {
            log.info("No token found in request header, returning 401");
            handleUnauthorized(response, "未登录或登录已过期，请重新登录");
            return;
        }
        
        try {
            // 提取Token（去除Bearer前缀）
            String extractedToken = jwtUtil.extractTokenFromHeader(token);
            log.info("Extracted token (without prefix): {}", extractedToken != null ? extractedToken.substring(0, 20) + "..." : null);
            
            if (!StringUtils.hasText(extractedToken)) {
                log.info("Invalid token format, returning 401");
                handleUnauthorized(response, "未登录或登录已过期，请重新登录");
                return;
            }
            
            // 验证Token是否过期
            log.info("Validating token expiration status");
            if (jwtUtil.isTokenExpired(extractedToken)) {
                log.info("Token has expired, returning 401");
                handleUnauthorized(response, "未登录或登录已过期，请重新登录");
                return;
            }
            
            // 从Token中获取用户信息并设置到上下文
            Long empId = jwtUtil.getEmpIdFromToken(extractedToken);

            if (empId == null) {
                log.info("Invalid token content, returning 401");
                handleUnauthorized(response, "未登录或登录已过期，请重新登录");
                return;
            }
            
            log.info("Setting current user ID in context: {}", empId);
            BaseContext.setCurrentId(empId);
            
            // 设置Spring Security的SecurityContext
            org.springframework.security.core.Authentication authentication = 
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    empId, null, java.util.Collections.emptyList());
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (Exception e) {
            log.error("JWT Token验证失败 for request {}: {}", request.getRequestURI(), e.getMessage(), e);
            handleUnauthorized(response, "未登录或登录已过期，请重新登录");
            return;
        }

        try {
            // 继续执行过滤链
            filterChain.doFilter(request, response);
        } finally {
            // 清除上下文
            BaseContext.removeCurrentId();
            // 不要清除SecurityContext，让Spring Security自己处理
        }
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteList(String uri) {
        log.info("Checking if path '{}' is in whitelist", uri);
        
        // 检查是否为白名单路径
        for (String pattern : WHITE_LIST) {
            // 构造完整的白名单路径（包含可能的context-path）
            String fullPattern = pattern;
            if (!pattern.startsWith("/")) {
                fullPattern = "/" + pattern;
            }
            
            if (pattern.endsWith("/")) {
                // 前缀匹配（如 /webjars/）
                if (uri.startsWith(fullPattern) || uri.startsWith("/api" + fullPattern)) {
                    log.info("Path '{}' matches white list pattern '{}' (prefix match)", uri, pattern);
                    return true;
                }
            } else {
                // 精确匹配
                if (uri.equals(fullPattern) || uri.equals("/api" + fullPattern) || 
                    uri.startsWith(fullPattern + "/") || uri.startsWith("/api" + fullPattern + "/")) {
                    log.info("Path '{}' matches white list pattern '{}' (exact match)", uri, pattern);
                    return true;
                }
            }
        }
        
        log.info("Path '{}' is not in white list", uri);
        return false;
    }

    /**
     * 处理未授权响应
     */
    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

