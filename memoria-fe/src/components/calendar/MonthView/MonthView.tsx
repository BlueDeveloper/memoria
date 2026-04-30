'use client';

import { useMemo } from 'react';
import {
  format,
  startOfMonth,
  endOfMonth,
  startOfWeek,
  endOfWeek,
  eachDayOfInterval,
  isSameMonth,
  isToday,
  isSameDay,
  parseISO,
} from 'date-fns';
import { ko } from 'date-fns/locale';
import { useCalendarStore } from '@/store/calendarStore';
import { CalendarEvent } from '@/types/calendar';
import styles from './MonthView.module.css';

const WEEKDAY_LABELS = ['일', '월', '화', '수', '목', '금', '토'];
const MAX_VISIBLE_EVENTS = 3;

export default function MonthView() {
  const currentDate = useCalendarStore((s) => s.currentDate);
  const events = useCalendarStore((s) => s.events);
  const visibleCalendarIds = useCalendarStore((s) => s.visibleCalendarIds);
  const calendars = useCalendarStore((s) => s.calendars);
  const setCurrentDate = useCalendarStore((s) => s.setCurrentDate);

  const days = useMemo(() => {
    const monthStart = startOfMonth(currentDate);
    const monthEnd = endOfMonth(currentDate);
    const calStart = startOfWeek(monthStart, { weekStartsOn: 0 });
    const calEnd = endOfWeek(monthEnd, { weekStartsOn: 0 });
    return eachDayOfInterval({ start: calStart, end: calEnd });
  }, [currentDate]);

  const filteredEvents = useMemo(
    () => events.filter((e) => visibleCalendarIds.has(e.calendarId)),
    [events, visibleCalendarIds]
  );

  const getEventsForDay = (day: Date): CalendarEvent[] => {
    return filteredEvents.filter((event) => {
      const start = parseISO(event.startDt);
      const end = parseISO(event.endDt);
      return isSameDay(day, start) || (day >= start && day <= end);
    });
  };

  const getEventColor = (event: CalendarEvent): string => {
    if (event.color) return event.color;
    const cal = calendars.find((c) => c.calendarId === event.calendarId);
    return cal?.color ?? 'var(--color-primary)';
  };

  const handleDayClick = (day: Date) => {
    setCurrentDate(day);
    // TODO: 이벤트 생성 모달 열기
  };

  return (
    <div className={styles.container}>
      <div className={styles.weekdayHeader}>
        {WEEKDAY_LABELS.map((label, i) => (
          <div
            key={label}
            className={`${styles.weekdayCell} ${i === 0 ? styles.sunday : ''} ${i === 6 ? styles.saturday : ''}`}
          >
            {label}
          </div>
        ))}
      </div>

      <div className={styles.grid}>
        {days.map((day) => {
          const sameMonth = isSameMonth(day, currentDate);
          const today = isToday(day);
          const dayEvents = getEventsForDay(day);
          const dayOfWeek = day.getDay();

          return (
            <div
              key={day.toISOString()}
              className={`${styles.cell} ${!sameMonth ? styles.cellOther : ''}`}
              onClick={() => handleDayClick(day)}
            >
              <span
                className={`${styles.dayNumber} ${today ? styles.dayToday : ''} ${dayOfWeek === 0 ? styles.sunday : ''} ${dayOfWeek === 6 ? styles.saturday : ''}`}
              >
                {format(day, 'd')}
              </span>

              <div className={styles.eventList}>
                {dayEvents.slice(0, MAX_VISIBLE_EVENTS).map((event) => (
                  <div
                    key={event.eventId}
                    className={styles.eventBar}
                    style={{ backgroundColor: getEventColor(event) }}
                    onClick={(e) => {
                      e.stopPropagation();
                      // TODO: 이벤트 상세 모달
                    }}
                    title={event.title}
                  >
                    {event.allDay
                      ? event.title
                      : `${format(parseISO(event.startDt), 'HH:mm')} ${event.title}`}
                  </div>
                ))}
                {dayEvents.length > MAX_VISIBLE_EVENTS && (
                  <div className={styles.moreEvents}>
                    +{dayEvents.length - MAX_VISIBLE_EVENTS}개 더보기
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
