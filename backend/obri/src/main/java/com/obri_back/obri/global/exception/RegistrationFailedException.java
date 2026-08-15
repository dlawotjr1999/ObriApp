package com.obri_back.obri.global.exception;

/*
 * 회원가입 처리 중 예기치 못한 실패 시 발생 (Firebase는 성공, MySQL 저장 단계 실패)
 * 500 Internal Server Error — 메시지가 전역 Exception 핸들러의 문구로 덮이지 않도록 전용 타입으로 분리
 */
public class RegistrationFailedException extends RuntimeException {
    public RegistrationFailedException(String message) {
        super(message);
    }
}
