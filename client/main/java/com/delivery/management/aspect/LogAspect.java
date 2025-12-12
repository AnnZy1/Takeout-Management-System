package com.delivery.management.aspect;

import com.delivery.management.common.annotation.Log;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.entity.Employee;
import com.delivery.management.entity.OperationLog;
import com.delivery.management.mapper.EmployeeMapper;
import com.delivery.management.mapper.OperationLogMapper;
import com.delivery.management.common.utils.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 定义切点：所有Controller方法
     */
    @Pointcut("execution(* com.delivery.management.controller..*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = IpUtil.getIpAddress(request);

        // 构建操作日志对象
        OperationLog operationLog = new OperationLog();
        operationLog.setIp(ip);
        operationLog.setContent(buildContent(methodName, args));
        operationLog.setOperationTime(LocalDateTime.now());
        operationLog.setModule(method.getDeclaringClass().getSimpleName());
        operationLog.setOperationType(methodName);

        try {
            // 获取当前用户信息
            Long userId = BaseContext.getCurrentId();
            if (userId != null) {
                operationLog.setOperatorId(userId);
                // 安全获取操作员姓名，避免因权限问题影响主业务
                try {
                    Employee employee = employeeMapper.selectById(userId);
                    if (employee != null) {
                        operationLog.setOperatorName(employee.getName());
                        operationLog.setBranchId(employee.getBranchId());
                    }
                } catch (Exception e) {
                    log.error("获取操作员信息失败: {}", e.getMessage(), e);
                }
            }

            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 操作成功
            operationLog.setStatus(1);
            return result;
        } catch (Exception e) {
            // 操作失败
            operationLog.setStatus(0);
            
            // 设置错误信息，限制长度以避免数据库字段过长
            if (e.getMessage() != null) {
                String errorMsg = e.getMessage();
                if (errorMsg.length() > 255) {
                    errorMsg = errorMsg.substring(0, 252) + "...";
                }
                operationLog.setErrorMsg(errorMsg);
            } else {
                operationLog.setErrorMsg(e.getClass().getSimpleName());
            }
            
            // 重新抛出异常，保证业务异常正常处理
            throw e;
        } finally {
            // 异步记录日志，避免影响主业务
            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception e) {
                log.error("记录操作日志失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 构建操作内容
     */
    private String buildContent(String methodName, Object[] args) {
        try {
            StringBuilder content = new StringBuilder();
            content.append(methodName);
            
            if (args != null && args.length > 0) {
                content.append("，参数：");
                for (int i = 0; i < args.length && i < 3; i++) {
                    if (args[i] != null) {
                        String argStr = args[i].toString();
                        // 截断过长的参数
                        if (argStr.length() > 50) {
                            argStr = argStr.substring(0, 50) + "...";
                        }
                        content.append(argStr);
                        if (i < args.length - 1 && i < 2) {
                            content.append("，");
                        }
                    }
                }
            }
            
            return content.toString();
        } catch (Exception e) {
            return methodName;
        }
    }
}

