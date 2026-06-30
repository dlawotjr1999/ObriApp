package com.obri_back.obri.application.dto;

import com.obri_back.obri.application.entity.ApplicationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppStatusUpdateDTO {
    @NotNull(message = "상태값을 입력해주세요")
    private ApplicationStatus status;  // ACCEPTED / REJECTED / CANCELLED
}
