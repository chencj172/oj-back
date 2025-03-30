package com.chencj.common.utils;


import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: Result
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 11:10
 * @Version: 1.0
 */
@Data
public class Result<T> implements Serializable {
    private Integer code;      // 状态码（如200=成功，500=错误）
    private String message;    // 提示信息（如"操作成功"）
    private T data;            // 返回的数据
    private Long timestamp;    // 时间戳

    public static final Integer SUCCESS = 200;
    public static final Integer ERROR = 500;
    public static final Integer BAD_REQUEST = 400;
    public static final Integer UNAUTHORIZED = 401;
    public static final Integer CONFLICT = 409;

    // 私有构造方法，强制使用静态方法创建对象
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    // ------------------------- 成功响应 -------------------------
    public static <T> Result<T> ok() {
        return build(null, SUCCESS, "");
    }

    public static <T> Result<T> ok(T data) {
        return build(data, SUCCESS, "");
    }

    public static <T> Result<T> ok(T data, String message) {
        return build(data, SUCCESS, message);
    }

    // ------------------------- 错误响应 -------------------------
    public static <T> Result<T> error(String message) {
        return build(null, ERROR, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return build(null, code, message);
    }

    // ------------------------- 链式调用支持 ----------------------
    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public Result<T> code(Integer code) {
        this.code = code;
        return this;
    }

    // ------------------------- 内部构建方法 ----------------------
    private static <T> Result<T> build(T data, Integer code, String message) {
        Result<T> r = new Result<>();
        r.data = data;
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> Result<T> build() {
        return new Result<>();
    }
}
