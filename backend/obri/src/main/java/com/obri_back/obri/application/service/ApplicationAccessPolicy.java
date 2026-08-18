package com.obri_back.obri.application.service;

import com.obri_back.obri.application.entity.Application;
import com.obri_back.obri.global.exception.ForbiddenException;
import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.user.entity.User;

import org.springframework.stereotype.Component;

/*
 * Application 관련 인가 판단 전담 (CLAUDE.md §8)
 * "누가 할 수 있는가"만 다루고, 판단 로직 자체는 Post·Application 엔티티에 위임(Tell-Don't-Ask) —
 * getUser().getId().equals(...) 같은 다단 체인을 이 클래스도 서비스도 직접 다루지 않는다.
 */
@Component
public class ApplicationAccessPolicy {

    // 구인글 작성자만 허용
    public void requireRecruiter(User user, Post post, String message) {
        if (!post.isOwnedBy(user)) {
            throw new ForbiddenException(message);
        }
    }

    // 지원서가 걸린 글의 구인자만 허용
    public void requireRecruiter(User user, Application application, String message) {
        if (!application.isRecruiter(user)) {
            throw new ForbiddenException(message);
        }
    }

    // 지원서의 지원자 본인만 허용
    public void requireApplicant(User user, Application application, String message) {
        if (!application.isApplicant(user)) {
            throw new ForbiddenException(message);
        }
    }

    // 구인자 또는 지원자 본인만 허용 (지원서 단건 조회)
    public void requireViewer(User user, Application application, String message) {
        if (!application.isApplicant(user) && !application.isRecruiter(user)) {
            throw new ForbiddenException(message);
        }
    }
}
