package com.brp.memoria.domain.diary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InviteAcceptRequest {

    @NotBlank(message = "초대 토큰은 필수 입력값입니다.")
    private String token;
}
