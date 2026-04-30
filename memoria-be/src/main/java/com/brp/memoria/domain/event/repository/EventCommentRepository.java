package com.brp.memoria.domain.event.repository;

import com.brp.memoria.domain.event.entity.Event;
import com.brp.memoria.domain.event.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventCommentRepository extends JpaRepository<EventComment, Long> {

    List<EventComment> findByEventAndDelYn(Event event, String delYn);
}
