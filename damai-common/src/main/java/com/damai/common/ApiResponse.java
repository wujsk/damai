package com.damai.common;


import com.damai.enums.BaseCode;
import lombok.Data;

/**
 * @author: haonan
 * @description: 响应类
 */
@Data
public class ApiResponse<T> {

    private Integer code;

    private String message;

    private T data;

    private ApiResponse() {
        //私有化构造方法
    }
    public static <T>ApiResponse<T> error(String message) {
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.code = -100;
        apiResponse.message = message;
        return apiResponse;
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.code = code;
        apiResponse.message = message;
        return apiResponse;
    }
    public static <T> ApiResponse<T> error(Integer code, T data) {

        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.code = -100;
        apiResponse.data = data;

        return apiResponse;
    }
    public static <T> ApiResponse<T> error(BaseCode baseCode) {
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.code = baseCode.getCode();
        apiResponse.message = baseCode.getMsg();
        return apiResponse;
    }
    public static <T> ApiResponse<T> error(BaseCode baseCode, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.code = baseCode.getCode();
        apiResponse.message = baseCode.getMsg();
        apiResponse.data = data;
        return apiResponse;
    }

    public static <T> ApiResponse<T> error() {
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.code = -100;
        apiResponse.message = "系统错误，请稍后重试!";
        return apiResponse;
    }

    public static <T> ApiResponse<T> ok() {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setCode(0);
        apiResponse.setData(null);
        return apiResponse;
    }

    public static <T> ApiResponse<T> ok(T t) {
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.code = 0;
        apiResponse.setData(t);
        return apiResponse;
    }
}
