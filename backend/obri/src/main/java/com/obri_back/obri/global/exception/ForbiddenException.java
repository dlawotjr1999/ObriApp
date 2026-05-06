package com.obri_back.obri.global.exception;

/*
 * 권한 없는 요청 시 발생
 * 403 Forbidden
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}