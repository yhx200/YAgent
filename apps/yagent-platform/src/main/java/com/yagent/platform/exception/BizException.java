package com.yagent.platform.exception;

public class BizException extends RuntimeException {

    private final String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(
            String code,
            String message,
            Throwable cause) {

        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
