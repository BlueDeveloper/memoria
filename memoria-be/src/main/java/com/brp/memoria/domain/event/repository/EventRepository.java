package com.brp.memoria.domain.event.repository;

import com.brp.memoria.domain.calendar.entity.Calendar;
import com.brp.memoria.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCalendarAndStartDtBetween(Calendar calendar, LocalDateTime startDt, LocalDateTime endDt);

    List<Event> findByCalendarAndDelYn(Calendar calendar, String delYn);
}
