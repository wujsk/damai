package com.damai.exception;


/**
 * @author: haonan
 * @description: 基础异常类
 */
public class BaseException extends RuntimeException {

    public BaseException() {

    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
