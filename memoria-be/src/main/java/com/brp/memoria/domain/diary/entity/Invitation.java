package com.brp.memoria.domain.diary.entity;

import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "INVITATION")
@SequenceGenerator(
        name = "SEQ_INVITATION_GENERATOR",
        sequenceName = "SEQ_INVITATION",
        allocationSize = 1
)
public class Invitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INVITATION_GENERATOR")
    @Column(name = "INVITATION_ID")
    private Long invitationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIARY_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVITER_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member inviter;

    @Column(name = "INVITEE_EMAIL", length = 255)
    private String inviteeEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "INVITE_TYPE", length = 20)
    private InviteType inviteType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "TOKEN", unique = true, nullable = false, length = 100)
    private String token;

    @Column(name = "EXPIRED_AT")
    private LocalDateTime expiredAt;

    @Builder
    public Invitation(Diary diary, Member inviter, String inviteeEmail,
                      InviteType inviteType, String token, LocalDateTime expiredAt) {
        this.diary = diary;
        this.inviter = inviter;
        this.inviteeEmail = inviteeEmail;
        this.inviteType = inviteType;
        this.status = InvitationStatus.PENDING;
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void reject() {
        this.status = InvitationStatus.REJECTED;
    }

    public void expire() {
        this.status = InvitationStatus.EXPIRED;
    }

    public boolean isExpired() {
        return this.expiredAt != null && LocalDateTime.now().isAfter(this.expiredAt);
    }

    public enum InviteType {
        KAKAO, EMAIL
    }

    public enum InvitationStatus {
        PENDING, ACCEPTED, REJECTED, EXPIRED
    }
}
