package com.delivery.management.common.context;

/**
 * 基于 ThreadLocal 封装工具类，用于保存和获取当前登录用户ID
 * 
 * @author system
 * @date 2025-01-15
 */
public class BaseContext {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前登录用户ID
     * 
     * @param id 用户ID
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取当前登录用户ID
     * 
     * @return 用户ID
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 清除当前线程的用户ID
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}

