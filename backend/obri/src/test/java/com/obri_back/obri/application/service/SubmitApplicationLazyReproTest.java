package com.obri_back.obri.application.service;

import com.obri_back.obri.application.dto.AppRequestDTO;
import com.obri_back.obri.application.dto.AppResponseDTO;
import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostInfo;
import com.obri_back.obri.post.entity.PostInstrument;
import com.obri_back.obri.post.repository.PostRepository;
import com.obri_back.obri.user.entity.Career;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.CareerRepository;
import com.obri_back.obri.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/*
 * BACKLOG.md #1 재현용 진단 테스트 — 커밋 대상 아님, 확인 후 삭제 예정
 * FirebaseAuthFilter가 실제로 하는 것과 동일하게 "짧은 조회 → 반환 즉시 detach"된
 * User 인스턴스를 submitApplication에 그대로 넘겨서 LazyInitializationException을 재현
 */
@SpringBootTest
class SubmitApplicationLazyReproTest {

    @Autowired UserRepository userRepository;
    @Autowired CareerRepository careerRepository;
    @Autowired PostRepository postRepository;
    @Autowired ApplicationService applicationService;

    @Test
    void submitApplication_withDetachedUser_reproducesLazyInitializationException() {
        User recruiter = userRepository.save(User.builder()
                .firebaseUid("repro-recruiter-uid")
                .phoneNumber("010-0000-0001")
                .nickname("repro-recruiter")
                .instrument("violin")
                .school("school")
                .isGraduate(false)
                .build());

        Post post = Post.create(recruiter, PostInfo.builder()
                .category("결혼")
                .title("재현용 구인글")
                .eventAt(LocalDateTime.now().plusDays(30))
                .location("서울")
                .timetable("본식 14:00")
                .pay(100000)
                .build());
        post.addInstrument(PostInstrument.of(post, "cello", 1));
        postRepository.save(post);

        User applicant = userRepository.save(User.builder()
                .firebaseUid("repro-applicant-uid")
                .phoneNumber("010-0000-0002")
                .nickname("repro-applicant")
                .instrument("cello")
                .school("school")
                .isGraduate(false)
                .build());
        careerRepository.save(Career.of(applicant, "orchestra", "2023 guest"));

        // FirebaseAuthFilter.doFilterInternal()과 동일한 모양: 별도의 단발성 조회 → 반환 즉시 detach
        User detachedApplicant = userRepository.findByFirebaseUid("repro-applicant-uid").orElseThrow();

        AppRequestDTO request = AppRequestDTO.from(post.getId(), "재현 테스트");

        // 컨트롤러가 @AuthenticationPrincipal로 넘기는 것과 동일하게 detached 인스턴스를 그대로 전달
        AppResponseDTO response = applicationService.submitApplication(detachedApplicant, request);
        System.out.println("REPRO RESULT (no exception): " + response.getApplicant().getCareers());
    }
}
