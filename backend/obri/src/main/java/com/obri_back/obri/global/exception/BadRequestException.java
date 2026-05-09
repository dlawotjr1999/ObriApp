package com.obri_back.obri.global.exception;

/*
 * 잘못된 요청 시 발생
 * 400 Bad Request
 * 예: 마감된 구인글에 지원, 본인 글에 지원
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}