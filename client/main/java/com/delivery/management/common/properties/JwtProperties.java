package com.delivery.management.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 * 
 * @author system
 * @date 2025-01-15
 */
@Component
@ConfigurationProperties(prefix = "delivery.jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secretKey = "deliveryManagementSecretKey2025";

    /**
     * JWT 过期时间（单位：毫秒）
     * 默认 2 小时
     */
    private Long ttl = 7200000L;

    /**
     * JWT 令牌前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * JWT 令牌请求头名称
     */
    private String tokenHeaderName = "Authorization";

    // 显式添加getter和setter方法，确保IDE能正确识别
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public String getTokenHeaderName() {
        return tokenHeaderName;
    }

    public void setTokenHeaderName(String tokenHeaderName) {
        this.tokenHeaderName = tokenHeaderName;
    }
}

