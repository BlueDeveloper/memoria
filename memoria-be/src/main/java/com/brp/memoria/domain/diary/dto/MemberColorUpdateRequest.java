package com.brp.memoria.domain.diary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberColorUpdateRequest {

    @NotBlank(message = "색상은 필수 입력값입니다.")
    private String color;
}
