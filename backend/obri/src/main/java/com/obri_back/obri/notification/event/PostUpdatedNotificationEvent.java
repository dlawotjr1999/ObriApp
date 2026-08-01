package com.obri_back.obri.notification.event;

import java.util.List;

// 구인글 수정 알림 발송 의도 — BACKLOG.md #15, 커밋 후(AFTER_COMMIT)에만 실제 발송
public record PostUpdatedNotificationEvent(List<String> fcmTokens, Long postId, String title) {
}
