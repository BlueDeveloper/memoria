package com.brp.memoria.domain.diary.entity;

import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIARY_MEMBER")
@SequenceGenerator(
        name = "SEQ_DIARY_MEMBER_GENERATOR",
        sequenceName = "SEQ_DIARY_MEMBER",
        allocationSize = 1
)
public class DiaryMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DIARY_MEMBER_GENERATOR")
    @Column(name = "DIARY_MEMBER_ID")
    private Long diaryMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIARY_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    private DiaryRole role;

    @Column(name = "COLOR", length = 20)
    private String color;

    @Column(name = "DEL_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String delYn;

    @Builder
    public DiaryMember(Diary diary, Member member, DiaryRole role, String color) {
        this.diary = diary;
        this.member = member;
        this.role = role != null ? role : DiaryRole.MEMBER;
        this.color = color;
        this.delYn = "N";
    }

    public void updateRole(DiaryRole role) {
        this.role = role;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void delete() {
        this.delYn = "Y";
    }

    public enum DiaryRole {
        OWNER, ADMIN, MEMBER
    }
}
