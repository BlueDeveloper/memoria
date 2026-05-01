package com.brp.memoria.domain.auth.dto;

import com.brp.memoria.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }
}
