package com.obri_back.obri.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.user.entity.User;

/*
 * User 저장소 — firebase_uid/nickname/email 기준 조회·중복 체크 제공
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirebaseUid(String firebaseUid);
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    boolean existsByFirebaseUid(String firebaseUid);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
