package com.obri_back.obri.notification.event;

// 신규 구인글 broadcast 알림 발송 의도 — BACKLOG.md #33, 커밋 후(AFTER_COMMIT)에만 실제 발송
public record NewPostNotificationEvent(Long postId, String title) {
}
