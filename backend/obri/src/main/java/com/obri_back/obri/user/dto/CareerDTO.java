package com.obri_back.obri.user.dto;

import com.obri_back.obri.user.entity.Career;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/*
 * 경력 요청/응답 공용 DTO (id가 있으면 기존, 없으면 신규)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerDTO {
    private Long id;          // nullable, 없으면 새로 추가

    private String organization;
    private String contexts;

    // Career 엔티티 → DTO 변환
    public static CareerDTO from(Career career) {
        return CareerDTO.builder()
                .id(career.getId())
                .organization(career.getOrganization())
                .contexts(career.getContexts())
                .build();
    }
}
