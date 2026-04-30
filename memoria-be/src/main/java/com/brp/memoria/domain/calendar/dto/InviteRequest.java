package com.brp.memoria.domain.calendar.dto;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InviteRequest {

    private String inviteType;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String inviteeEmail;
}
