package com.obri_back.obri.application.entity;

// 지원 상태 — 전이는 행위자·출발 상태별로 고정 (명세 Application §2.4)
public enum ApplicationStatus {
    PENDING,    // 대기 (제출 직후 기본값)
    ACCEPTED,   // 수락 (구인자, PENDING에서)
    REJECTED,   // 거절 (구인자, PENDING에서)
    CANCELLED,  // 취소 (지원자, PENDING에서만)
    REVOKED,    // 철회 (구인자, ACCEPTED에서만; 확정 취소·자리 재오픈)
}
