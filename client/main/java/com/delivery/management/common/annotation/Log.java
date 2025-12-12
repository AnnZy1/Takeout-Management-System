package com.delivery.management.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 
 * @author system
 * @date 2025-01-15
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 操作类型
     */
    String operationType() default "";
}

