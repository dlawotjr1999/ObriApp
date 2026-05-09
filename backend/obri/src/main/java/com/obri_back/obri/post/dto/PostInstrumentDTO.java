package com.obri_back.obri.post.dto;

import com.obri_back.obri.post.entity.PostInstrument;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostInstrumentDTO {

    private Long id;          
    private String instrument;
    private Integer people;

    public static PostInstrumentDTO from(PostInstrument postInstrument) {
        return PostInstrumentDTO.builder()
                .id(postInstrument.getId())
                .instrument(postInstrument.getInstrument())
                .people(postInstrument.getPeople())
                .build();
    }
}