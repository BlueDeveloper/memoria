package com.brp.memoria.domain.calendar.repository;

import com.brp.memoria.domain.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Optional<Calendar> findByInviteCode(String inviteCode);
}
