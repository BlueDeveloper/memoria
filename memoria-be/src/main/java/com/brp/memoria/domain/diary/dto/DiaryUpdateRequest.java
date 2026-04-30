package com.brp.memoria.domain.diary.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryUpdateRequest {

    @Size(max = 200, message = "다이어리 이름은 200자 이하로 입력해주세요.")
    private String name;

    @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요.")
    private String description;

    private String color;
}
