package com.brp.memoria.domain.member.entity;

import com.brp.memoria.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "MEMBER")
@SequenceGenerator(
        name = "SEQ_MEMBER_GENERATOR",
        sequenceName = "SEQ_MEMBER",
        allocationSize = 1
)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_GENERATOR")
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "PASSWORD", length = 255)
    private String password;

    @Column(name = "NICKNAME", nullable = false, length = 100)
    private String nickname;

    @Column(name = "PROFILE_IMAGE", length = 500)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER", length = 20)
    private Provider provider;

    @Column(name = "PROVIDER_ID", length = 255)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    private Role role;

    @Column(name = "DEL_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String delYn;

    @Builder
    public Member(String email, String password, String nickname, String profileImage,
                  Provider provider, String providerId, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role != null ? role : Role.USER;
        this.delYn = "N";
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.delYn = "Y";
    }

    public enum Provider {
        KAKAO, GOOGLE, APPLE
    }

    public enum Role {
        USER, ADMIN
    }
}
