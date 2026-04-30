package com.brp.memoria.domain.event.entity;

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
@Table(name = "EVENT_COMMENT")
@SequenceGenerator(
        name = "SEQ_EVENT_COMMENT_GENERATOR",
        sequenceName = "SEQ_EVENT_COMMENT",
        allocationSize = 1
)
public class EventComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENT_COMMENT_GENERATOR")
    @Column(name = "EVENT_COMMENT_ID")
    private Long eventCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    @Column(name = "DEL_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String delYn;

    @Builder
    public EventComment(Event event, Member member, String content) {
        this.event = event;
        this.member = member;
        this.content = content;
        this.delYn = "N";
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.delYn = "Y";
    }
}
