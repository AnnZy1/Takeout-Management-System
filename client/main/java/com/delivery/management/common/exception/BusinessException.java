package com.delivery.management.common.exception;

/**
 * 业务异常类
 * 
 * @author system
 * @date 2025-01-15
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String msg;

    public BusinessException() {
        super();
    }

    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
        this.code = 500;
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
        this.code = 500;
    }

    public BusinessException(Integer code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

