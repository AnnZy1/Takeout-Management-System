package com.delivery.management.common.utils;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败尝试缓存
 * 用于记录登录失败次数，实现账号锁定功能
 * 
 * @author system
 * @date 2025-01-15
 */
public class LoginAttemptCache {

    private static final ConcurrentHashMap<String, LoginAttempt> cache = new ConcurrentHashMap<>();

    /**
     * 最大失败次数
     */
    private static final int MAX_ATTEMPTS = 3;

    /**
     * 锁定时间（分钟）
     */
    private static final int LOCK_DURATION_MINUTES = 15;

    /**
     * 记录登录失败
     * 
     * @param username 用户名
     * @return true-账号被锁定，false-未锁定
     */
    public static boolean recordFailure(String username) {
        LoginAttempt attempt = cache.computeIfAbsent(username, k -> new LoginAttempt());
        attempt.setFailureCount(attempt.getFailureCount() + 1);
        attempt.setLastAttemptTime(LocalDateTime.now());

        if (attempt.getFailureCount() >= MAX_ATTEMPTS) {
            attempt.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            return true;
        }
        return false;
    }

    /**
     * 清除登录失败记录（登录成功时调用）
     * 
     * @param username 用户名
     */
    public static void clearAttempts(String username) {
        cache.remove(username);
    }

    /**
     * 检查账号是否被锁定
     * 
     * @param username 用户名
     * @return true-已锁定，false-未锁定
     */
    public static boolean isLocked(String username) {
        LoginAttempt attempt = cache.get(username);
        if (attempt == null) {
            return false;
        }

        // 检查锁定时间是否已过期
        if (attempt.getLockedUntil() != null && attempt.getLockedUntil().isAfter(LocalDateTime.now())) {
            return true;
        }

        // 锁定时间已过期，清除记录
        if (attempt.getLockedUntil() != null && attempt.getLockedUntil().isBefore(LocalDateTime.now())) {
            cache.remove(username);
            return false;
        }

        return false;
    }

    /**
     * 获取剩余锁定时间（分钟）
     * 
     * @param username 用户名
     * @return 剩余锁定时间，未锁定返回0
     */
    public static long getRemainingLockTime(String username) {
        LoginAttempt attempt = cache.get(username);
        if (attempt == null || attempt.getLockedUntil() == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (attempt.getLockedUntil().isAfter(now)) {
            return java.time.Duration.between(now, attempt.getLockedUntil()).toMinutes();
        }

        return 0;
    }

    /**
     * 获取剩余尝试次数
     * 
     * @param username 用户名
     * @return 剩余尝试次数
     */
    public static int getRemainingAttempts(String username) {
        LoginAttempt attempt = cache.get(username);
        if (attempt == null) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempt.getFailureCount());
    }

    @Data
    static class LoginAttempt {
        private int failureCount = 0;
        private LocalDateTime lastAttemptTime;
        private LocalDateTime lockedUntil;
    }
}

