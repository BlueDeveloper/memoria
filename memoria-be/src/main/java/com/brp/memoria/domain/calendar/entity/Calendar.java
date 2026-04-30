package com.brp.memoria.domain.calendar.entity;

import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ForeignKey;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "CALENDAR")
@SequenceGenerator(
        name = "SEQ_CALENDAR_GENERATOR",
        sequenceName = "SEQ_CALENDAR",
        allocationSize = 1
)
public class Calendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CALENDAR_GENERATOR")
    @Column(name = "CALENDAR_ID")
    private Long calendarId;

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
            foreignKey = @jakarta.persistence.ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member owner;

    @Column(name = "DEL_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String delYn;

    @Builder
    public Calendar(String name, String description, String color, String inviteCode, Member owner) {
        this.name = name;
        this.description = description;
        this.color = color != null ? color : "#4A90D9";
        this.inviteCode = inviteCode;
        this.owner = owner;
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

    public void delete() {
        this.delYn = "Y";
    }
}
