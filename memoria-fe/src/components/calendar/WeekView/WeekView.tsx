'use client';

import { useMemo, useEffect, useState, useRef } from 'react';
import {
  format,
  startOfWeek,
  endOfWeek,
  eachDayOfInterval,
  isToday,
  parseISO,
  isSameDay,
  getHours,
  getMinutes,
  differenceInMinutes,
} from 'date-fns';
import { ko } from 'date-fns/locale';
import { useCalendarStore } from '@/store/calendarStore';
import { useAuthStore } from '@/store/authStore';
import { CalendarEvent } from '@/types/calendar';
import EventModal from '@/components/calendar/EventModal/EventModal';
import EventDetailModal from '@/components/calendar/EventDetailModal/EventDetailModal';
import AuthPromptModal from '@/components/calendar/AuthPromptModal/AuthPromptModal';
import styles from './WeekView.module.css';

const HOURS = Array.from({ length: 24 }, (_, i) => i);
const SLOT_HEIGHT = 60; // px per hour

export default function WeekView() {
  const currentDate = useCalendarStore((s) => s.currentDate);
  const events = useCalendarStore((s) => s.events);
  const visibleCalendarIds = useCalendarStore((s) => s.visibleCalendarIds);
  const calendars = useCalendarStore((s) => s.calendars);
  const gridRef = useRef<HTMLDivElement>(null);
  const [now, setNow] = useState(new Date());
  const [showEventModal, setShowEventModal] = useState(false);
  const [modalDate, setModalDate] = useState<Date | null>(null);
  const [modalHour, setModalHour] = useState<number>(9);
  const [selectedEvent, setSelectedEvent] = useState<CalendarEvent | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showAuthPrompt, setShowAuthPrompt] = useState(false);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  const weekDays = useMemo(() => {
    const start = startOfWeek(currentDate, { weekStartsOn: 0 });
    const end = endOfWeek(currentDate, { weekStartsOn: 0 });
    return eachDayOfInterval({ start, end });
  }, [currentDate]);

  const filteredEvents = useMemo(
    () => events.filter((e) => visibleCalendarIds.has(e.calendarId)),
    [events, visibleCalendarIds]
  );

  const allDayEvents = useMemo(
    () => filteredEvents.filter((e) => e.allDay),
    [filteredEvents]
  );

  const timedEvents = useMemo(
    () => filteredEvents.filter((e) => !e.allDay),
    [filteredEvents]
  );

  // 현재 시간선 업데이트 (1분마다)
  useEffect(() => {
    const timer = setInterval(() => setNow(new Date()), 60000);
    return () => clearInterval(timer);
  }, []);

  // 초기 스크롤: 현재 시간 근처로
  useEffect(() => {
    if (gridRef.current) {
      const currentHour = new Date().getHours();
      const scrollTo = Math.max(0, (currentHour - 1) * SLOT_HEIGHT);
      gridRef.current.scrollTop = scrollTo;
    }
  }, []);

  const getEventsForDay = (day: Date): CalendarEvent[] => {
    return timedEvents.filter((event) => {
      const start = parseISO(event.startDt);
      return isSameDay(day, start);
    });
  };

  const getAllDayEventsForDay = (day: Date): CalendarEvent[] => {
    return allDayEvents.filter((event) => {
      const start = parseISO(event.startDt);
      const end = parseISO(event.endDt);
      return day >= start && day <= end;
    });
  };

  const getEventColor = (event: CalendarEvent): string => {
    if (event.color) return event.color;
    const cal = calendars.find((c) => c.calendarId === event.calendarId);
    return cal?.color ?? 'var(--color-primary)';
  };

  const getEventPosition = (event: CalendarEvent) => {
    const start = parseISO(event.startDt);
    const end = parseISO(event.endDt);
    const startMinutes = getHours(start) * 60 + getMinutes(start);
    const duration = Math.max(differenceInMinutes(end, start), 30);
    return {
      top: (startMinutes / 60) * SLOT_HEIGHT,
      height: (duration / 60) * SLOT_HEIGHT,
    };
  };

  const nowMinutes = getHours(now) * 60 + getMinutes(now);
  const nowLineTop = (nowMinutes / 60) * SLOT_HEIGHT;

  const handleSlotClick = (day: Date, hour: number) => {
    if (!isAuthenticated) {
      setShowAuthPrompt(true);
      return;
    }
    setModalDate(day);
    setModalHour(hour);
    setShowEventModal(true);
  };

  const handleEventClick = (event: CalendarEvent) => {
    setSelectedEvent(event);
    setShowDetailModal(true);
  };

  return (
    <div className={styles.container}>
      {/* 종일 이벤트 영역 */}
      <div className={styles.allDaySection}>
        <div className={styles.allDayLabel}>종일</div>
        <div className={styles.allDayGrid}>
          {weekDays.map((day) => {
            const dayAllDay = getAllDayEventsForDay(day);
            return (
              <div
                key={day.toISOString()}
                className={`${styles.allDayCell} ${isToday(day) ? styles.todayColumn : ''}`}
              >
                {dayAllDay.map((event) => (
                  <div
                    key={event.eventId}
                    className={styles.allDayEvent}
                    style={{ backgroundColor: getEventColor(event) }}
                  >
                    {event.title}
                  </div>
                ))}
              </div>
            );
          })}
        </div>
      </div>

      {/* 헤더: 요일 + 날짜 */}
      <div className={styles.dayHeader}>
        <div className={styles.timeGutter} />
        {weekDays.map((day) => {
          const today = isToday(day);
          const dayOfWeek = day.getDay();
          return (
            <div
              key={day.toISOString()}
              className={`${styles.dayHeaderCell} ${today ? styles.todayHeader : ''}`}
            >
              <span
                className={`${styles.dayLabel} ${dayOfWeek === 0 ? styles.sunday : ''} ${dayOfWeek === 6 ? styles.saturday : ''}`}
              >
                {format(day, 'EEE', { locale: ko })}
              </span>
              <span className={`${styles.dayDate} ${today ? styles.dayDateToday : ''}`}>
                {format(day, 'd')}
              </span>
            </div>
          );
        })}
      </div>

      {/* 시간 그리드 */}
      <div className={styles.gridWrapper} ref={gridRef}>
        <div className={styles.grid} style={{ height: 24 * SLOT_HEIGHT }}>
          {/* 시간축 */}
          <div className={styles.timeGutter}>
            {HOURS.map((hour) => (
              <div
                key={hour}
                className={styles.timeLabel}
                style={{ top: hour * SLOT_HEIGHT, height: SLOT_HEIGHT }}
              >
                {hour === 0 ? '' : `${String(hour).padStart(2, '0')}:00`}
              </div>
            ))}
          </div>

          {/* 날짜 열 */}
          {weekDays.map((day) => {
            const today = isToday(day);
            const dayEvents = getEventsForDay(day);

            return (
              <div
                key={day.toISOString()}
                className={`${styles.dayColumn} ${today ? styles.todayColumn : ''}`}
              >
                {/* 시간 슬롯 */}
                {HOURS.map((hour) => (
                  <div
                    key={hour}
                    className={styles.slot}
                    style={{ top: hour * SLOT_HEIGHT, height: SLOT_HEIGHT }}
                    onClick={() => handleSlotClick(day, hour)}
                  />
                ))}

                {/* 이벤트 블록 */}
                {dayEvents.map((event) => {
                  const pos = getEventPosition(event);
                  return (
                    <div
                      key={event.eventId}
                      className={styles.eventBlock}
                      style={{
                        top: pos.top,
                        height: pos.height,
                        backgroundColor: getEventColor(event),
                      }}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleEventClick(event);
                      }}
                    >
                      <span className={styles.eventTime}>
                        {format(parseISO(event.startDt), 'HH:mm')}
                      </span>
                      <span className={styles.eventTitle}>{event.title}</span>
                    </div>
                  );
                })}

                {/* 현재 시간선 */}
                {today && (
                  <div className={styles.nowLine} style={{ top: nowLineTop }}>
                    <div className={styles.nowDot} />
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>

      {showEventModal && (
        <EventModal
          onClose={() => setShowEventModal(false)}
          initialDate={modalDate ?? undefined}
          initialHour={modalHour}
        />
      )}

      {showAuthPrompt && (
        <AuthPromptModal onClose={() => setShowAuthPrompt(false)} />
      )}

      {showDetailModal && selectedEvent && (
        <EventDetailModal
          event={selectedEvent}
          onClose={() => { setShowDetailModal(false); setSelectedEvent(null); }}
          onEdit={() => {
            setShowDetailModal(false);
            setSelectedEvent(null);
            setShowEventModal(true);
          }}
        />
      )}
    </div>
  );
}
