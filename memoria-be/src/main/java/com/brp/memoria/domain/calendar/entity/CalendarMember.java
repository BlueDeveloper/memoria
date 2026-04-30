package com.brp.memoria.domain.calendar.entity;

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
@Table(name = "CALENDAR_MEMBER")
@SequenceGenerator(
        name = "SEQ_CALENDAR_MEMBER_GENERATOR",
        sequenceName = "SEQ_CALENDAR_MEMBER",
        allocationSize = 1
)
public class CalendarMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CALENDAR_MEMBER_GENERATOR")
    @Column(name = "CALENDAR_MEMBER_ID")
    private Long calendarMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Calendar calendar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    private CalendarRole role;

    @Column(name = "COLOR", length = 20)
    private String color;

    @Column(name = "DEL_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String delYn;

    @Builder
    public CalendarMember(Calendar calendar, Member member, CalendarRole role, String color) {
        this.calendar = calendar;
        this.member = member;
        this.role = role != null ? role : CalendarRole.MEMBER;
        this.color = color;
        this.delYn = "N";
    }

    public void updateRole(CalendarRole role) {
        this.role = role;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void delete() {
        this.delYn = "Y";
    }

    public enum CalendarRole {
        OWNER, ADMIN, MEMBER
    }
}
