package com.obri_back.obri.user.dto;

import com.obri_back.obri.user.entity.Career;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerDTO {
    private Long id;          // nullable, 없으면 새로 추가

    private String organization;
    private String contexts;

    public static CareerDTO from(Career career) {
        return CareerDTO.builder()
                .id(career.getId())
                .organization(career.getOrganization())
                .contexts(career.getContexts())
                .build();
    }
}
