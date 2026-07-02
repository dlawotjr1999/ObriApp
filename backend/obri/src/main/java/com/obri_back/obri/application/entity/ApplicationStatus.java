package com.obri_back.obri.application.entity;

public enum ApplicationStatus {
    PENDING,
    ACCEPTED,   // 수락 (구인자, PENDING에서)
    REJECTED,   // 거절 (구인자, PENDING에서)
    CANCELLED,  // 취소 (지원자, PENDING에서만)
    REVOKED,    // 철회 (구인자, ACCEPTED에서만; 확정 취소·자리 재오픈)
}
