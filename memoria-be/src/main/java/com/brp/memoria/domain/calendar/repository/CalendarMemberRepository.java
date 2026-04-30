package com.brp.memoria.domain.calendar.repository;

import com.brp.memoria.domain.calendar.entity.Calendar;
import com.brp.memoria.domain.calendar.entity.CalendarMember;
import com.brp.memoria.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarMemberRepository extends JpaRepository<CalendarMember, Long> {

    Optional<CalendarMember> findByCalendarAndMember(Calendar calendar, Member member);

    List<CalendarMember> findByMemberAndDelYn(Member member, String delYn);

    List<CalendarMember> findByCalendarAndDelYn(Calendar calendar, String delYn);
}
