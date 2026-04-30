package com.brp.memoria.domain.diary.dto;

import com.brp.memoria.domain.diary.entity.Invitation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvitationResponse {

    private Long invitationId;
    private String diaryName;
    private String inviterNickname;
    private String token;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;

    public static InvitationResponse from(Invitation invitation) {
        return new InvitationResponse(
                invitation.getInvitationId(),
                invitation.getDiary().getName(),
                invitation.getInviter().getNickname(),
                invitation.getToken(),
                invitation.getExpiredAt(),
                invitation.getCreDt()
        );
    }
}
