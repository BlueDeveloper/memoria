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
@Table(name = "DIARY")
@SequenceGenerator(
        name = "SEQ_DIARY_GENERATOR",
        sequenceName = "SEQ_DIARY",
        allocationSize = 1
)
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DIARY_GENERATOR")
    @Column(name = "DIARY_ID")
    private Long diaryId;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "COLOR", nullable = false, length = 20)
    private String color;

    @Column(name = "INVITE_CODE", unique = true, length = 50)
    private String inviteCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member owner;

    @Column(name = "LOGO_ID")
    private Long logoId;

    @Column(name = "THEME_ID")
    private Long themeId;

    @Column(name = "DIARY_TYPE", nullable = false, length = 20)
    private String diaryType;

    @Column(name = "DEL_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String delYn;

    @Builder
    public Diary(String name, String description, String color, String inviteCode,
                 Member owner, Long logoId, Long themeId, String diaryType) {
        this.name = name;
        this.description = description;
        this.color = color != null ? color : "#4A90D9";
        this.inviteCode = inviteCode;
        this.owner = owner;
        this.logoId = logoId;
        this.themeId = themeId;
        this.diaryType = diaryType != null ? diaryType : "GENERAL";
        this.delYn = "N";
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void updateInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public void updateLogoId(Long logoId) {
        this.logoId = logoId;
    }

    public void updateThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public void delete() {
        this.delYn = "Y";
    }
}
