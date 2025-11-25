package com.lyx.secondhandsystem.common;

// API响应类
public class ApiResponse<T> {
    private int code; // 状态码
    private String message; // 消息
    private T data; // 数据

    // 成功响应
    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.code = 200;
        r.message = "OK";
        r.data = data;
        return r;
    }

    // 错误响应
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.code = code;
        r.message = message;
        return r;
    }

    // 获取状态码
    public int getCode() { return code; }
    // 设置状态码
    public void setCode(int code) { this.code = code; }
    // 获取消息
    public String getMessage() { return message; }
    // 设置消息 
    public void setMessage(String message) { this.message = message; }
    // 获取数据 
    public T getData() { return data; }
    // 设置数据 
    public void setData(T data) { this.data = data; }
}
