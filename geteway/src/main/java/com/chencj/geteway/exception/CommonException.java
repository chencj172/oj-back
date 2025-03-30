package com.chencj.geteway.exception;

import lombok.Getter;

/**
 * @ClassName: CommonExecption
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 14:27
 * @Version: 1.0
 */

@Getter
public class CommonException extends RuntimeException{
    private int code;

    public CommonException(String message, int code) {
        super(message);
        this.code = code;
    }

    public CommonException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public CommonException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }
}
