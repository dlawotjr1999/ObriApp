package com.obri_back.obri.auth.dto;

import com.obri_back.obri.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private LocalDateTime createdAt;

    public static RegisterResponseDTO from(User user) {
        return RegisterResponseDTO.builder()
                .createdAt(user.getCreatedAt())
                .build();
    }
}
