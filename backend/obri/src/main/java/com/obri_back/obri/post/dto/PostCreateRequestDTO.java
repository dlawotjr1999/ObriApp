package com.obri_back.obri.post.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDTO {

    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotNull
    private LocalDateTime eventAt;

    @NotBlank
    private String location;

    @NotBlank
    private String timetable;

    @NotNull
    private Integer pay;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<InstrumentItem> instruments;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstrumentItem {
        @NotBlank
        private String instrument;

        @NotNull
        @Min(1)
        private Integer people;
    }
}
