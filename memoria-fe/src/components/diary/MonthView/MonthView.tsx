'use client';

import { useMemo, useState } from 'react';
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
import { useDiaryStore } from '@/store/diaryStore';
import { useAuthStore } from '@/store/authStore';
import { DiaryEvent } from '@/types/diary';
import EventModal from '@/components/diary/EventModal/EventModal';
import EventDetailModal from '@/components/diary/EventDetailModal/EventDetailModal';
import AuthPromptModal from '@/components/diary/AuthPromptModal/AuthPromptModal';
import styles from './MonthView.module.css';

const WEEKDAY_LABELS = ['일', '월', '화', '수', '목', '금', '토'];
const MAX_VISIBLE_EVENTS = 3;

export default function MonthView() {
  const currentDate = useDiaryStore((s) => s.currentDate);
  const events = useDiaryStore((s) => s.events);
  const visibleDiaryIds = useDiaryStore((s) => s.visibleDiaryIds);
  const diaries = useDiaryStore((s) => s.diaries);
  const setCurrentDate = useDiaryStore((s) => s.setCurrentDate);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  const [showEventModal, setShowEventModal] = useState(false);
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [selectedEvent, setSelectedEvent] = useState<DiaryEvent | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showAuthPrompt, setShowAuthPrompt] = useState(false);

  const days = useMemo(() => {
    const monthStart = startOfMonth(currentDate);
    const monthEnd = endOfMonth(currentDate);
    const calStart = startOfWeek(monthStart, { weekStartsOn: 0 });
    const calEnd = endOfWeek(monthEnd, { weekStartsOn: 0 });
    return eachDayOfInterval({ start: calStart, end: calEnd });
  }, [currentDate]);

  const filteredEvents = useMemo(
    () => events.filter((e) => visibleDiaryIds.has(e.diaryId)),
    [events, visibleDiaryIds]
  );

  const getEventsForDay = (day: Date): DiaryEvent[] => {
    return filteredEvents.filter((event) => {
      const start = parseISO(event.startDt);
      const end = parseISO(event.endDt);
      return isSameDay(day, start) || (day >= start && day <= end);
    });
  };

  const getEventColor = (event: DiaryEvent): string => {
    if (event.color) return event.color;
    const diary = diaries.find((d) => d.diaryId === event.diaryId);
    return diary?.color ?? 'var(--color-primary)';
  };

  const handleDayClick = (day: Date) => {
    setCurrentDate(day);
    if (!isAuthenticated) {
      setShowAuthPrompt(true);
      return;
    }
    setSelectedDate(day);
    setShowEventModal(true);
  };

  const handleEventClick = (event: DiaryEvent) => {
    setSelectedEvent(event);
    setShowDetailModal(true);
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
                      handleEventClick(event);
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

      {showEventModal && (
        <EventModal
          onClose={() => setShowEventModal(false)}
          initialDate={selectedDate ?? undefined}
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
            setShowEventModal(true);
          }}
        />
      )}
    </div>
  );
}
