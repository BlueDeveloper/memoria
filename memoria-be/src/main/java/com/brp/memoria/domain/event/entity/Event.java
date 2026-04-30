package com.brp.memoria.domain.event.entity;

import com.brp.memoria.domain.diary.entity.Diary;
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
@Table(name = "EVENT")
@SequenceGenerator(
        name = "SEQ_EVENT_GENERATOR",
        sequenceName = "SEQ_EVENT",
        allocationSize = 1
)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENT_GENERATOR")
    @Column(name = "EVENT_ID")
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIARY_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Diary diary;

    @Column(name = "TITLE", nullable = false, length = 300)
    private String title;

    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    @Column(name = "LOCATION", length = 500)
    private String location;

    @Column(name = "START_DT", nullable = false)
    private LocalDateTime startDt;

    @Column(name = "END_DT", nullable = false)
    private LocalDateTime endDt;

    @Column(name = "ALL_DAY_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String allDayYn;

    @Column(name = "COLOR", length = 20)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "REPEAT_TYPE", length = 20)
    private RepeatType repeatType;

    @Column(name = "REMIND_MINUTES")
    private Integer remindMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATOR_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member creator;

    @Column(name = "DEL_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String delYn;

    @Builder
    public Event(Diary diary, String title, String description, String location,
                 LocalDateTime startDt, LocalDateTime endDt, String allDayYn, String color,
                 RepeatType repeatType, Integer remindMinutes, Member creator) {
        this.diary = diary;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDt = startDt;
        this.endDt = endDt;
        this.allDayYn = allDayYn != null ? allDayYn : "N";
        this.color = color;
        this.repeatType = repeatType;
        this.remindMinutes = remindMinutes;
        this.creator = creator;
        this.delYn = "N";
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateSchedule(LocalDateTime startDt, LocalDateTime endDt) {
        this.startDt = startDt;
        this.endDt = endDt;
    }

    public void updateAllDayYn(String allDayYn) {
        this.allDayYn = allDayYn;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void updateRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public void updateRemindMinutes(Integer remindMinutes) {
        this.remindMinutes = remindMinutes;
    }

    public void delete() {
        this.delYn = "Y";
    }

    public enum RepeatType {
        NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    }
}
