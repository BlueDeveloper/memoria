package com.brp.memoria.domain.event.dto;

import com.brp.memoria.domain.event.entity.EventComment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    private Long commentId;
    private Long eventId;
    private String memberNickname;
    private String memberProfileImage;
    private String content;
    private LocalDateTime createdAt;

    public static CommentResponse from(EventComment comment) {
        return new CommentResponse(
                comment.getEventCommentId(),
                comment.getEvent().getEventId(),
                comment.getMember().getNickname(),
                comment.getMember().getProfileImage(),
                comment.getContent(),
                comment.getCreDt()
        );
    }
}
