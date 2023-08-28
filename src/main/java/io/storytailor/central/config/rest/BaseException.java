package io.storytailor.central.config.rest;

import io.storytailor.central.code.ErrorCode;

public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 6697553987008675632L;

    ErrorCode errorCode = null;

    /**
     * Base exception with errorCode
     * 
     * @param errorCode ErrorCode
     */
    protected BaseException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Base exception with error code and message
     * 
     * @param errorCode ErrorCode
     * @param msg       Error message
     */
    protected BaseException(ErrorCode errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    /**
     * Base exception with error code and throwable
     * 
     * @param errorCode ErrorCode
     * @param throwable Throwable
     */
    protected BaseException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
    }

    /**
     * Base exception with error code and message and throwable
     * 
     * @param errorCode ErrorCode
     * @param msg       Error message
     * @param throwable Throwable
     */
    protected BaseException(ErrorCode errorCode, String msg, Throwable throwable) {
        super(msg, throwable);
        this.errorCode = errorCode;
    }

    /**
     * Get error code
     * 
     * @return Error code
     */
    public String getErrorCode() {
        return errorCode.getCode();
    }
}