package com.obri_back.obri.global.exception;

/*
 * 중복 데이터 → 409 예외 처리 공통 로직
 * uid·email·phoneNumber·nickname·schoolEmail 등 UNIQUE 필드 체크가 모두 동일한 형태라 하나로 모음
 */
public final class ConflictGuard {

    private ConflictGuard() {
    }

    public static void requireUnique(boolean alreadyExists, String message) {
        if (alreadyExists) {
            throw new ConflictException(message);
        }
    }
}
