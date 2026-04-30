package com.brp.memoria.domain.event.repository;

import com.brp.memoria.domain.diary.entity.Diary;
import com.brp.memoria.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByDiaryAndStartDtBetween(Diary diary, LocalDateTime startDt, LocalDateTime endDt);

    List<Event> findByDiaryAndDelYn(Diary diary, String delYn);
}
