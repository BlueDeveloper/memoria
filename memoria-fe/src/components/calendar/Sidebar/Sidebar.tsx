'use client';

import { useState, useMemo } from 'react';
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
  addMonths,
  subMonths,
} from 'date-fns';
import { ko } from 'date-fns/locale';
import { Plus, Settings, ChevronLeft, ChevronRight, Check } from 'lucide-react';
import { useCalendarStore } from '@/store/calendarStore';
import { useAuthStore } from '@/store/authStore';
import CreateCalendarModal from '@/components/calendar/CreateCalendarModal/CreateCalendarModal';
import styles from './Sidebar.module.css';

const WEEKDAY_LABELS = ['일', '월', '화', '수', '목', '금', '토'];

export default function Sidebar() {
  const calendars = useCalendarStore((s) => s.calendars);
  const visibleCalendarIds = useCalendarStore((s) => s.visibleCalendarIds);
  const toggleCalendarVisibility = useCalendarStore((s) => s.toggleCalendarVisibility);
  const setCurrentDate = useCalendarStore((s) => s.setCurrentDate);
  const currentDate = useCalendarStore((s) => s.currentDate);
  const user = useAuthStore((s) => s.user);

  const [miniDate, setMiniDate] = useState(new Date());
  const [showCreateModal, setShowCreateModal] = useState(false);

  const miniDays = useMemo(() => {
    const monthStart = startOfMonth(miniDate);
    const monthEnd = endOfMonth(miniDate);
    const calStart = startOfWeek(monthStart, { weekStartsOn: 0 });
    const calEnd = endOfWeek(monthEnd, { weekStartsOn: 0 });
    return eachDayOfInterval({ start: calStart, end: calEnd });
  }, [miniDate]);

  const handleMiniDayClick = (day: Date) => {
    setCurrentDate(day);
  };

  const nickname = user?.nickname ?? '사용자';

  return (
    <div className={styles.sidebar}>
      <div className={styles.logo}>Memoria</div>

      {/* 캘린더 목록 */}
      <div className={styles.section}>
        <div className={styles.sectionTitle}>내 캘린더</div>
        <div className={styles.calendarList}>
          {calendars.map((cal) => {
            const visible = visibleCalendarIds.has(cal.calendarId);
            return (
              <div
                key={cal.calendarId}
                className={styles.calendarItem}
                onClick={() => toggleCalendarVisibility(cal.calendarId)}
              >
                <div
                  className={`${styles.checkbox} ${visible ? styles.checkboxChecked : ''}`}
                  style={{ backgroundColor: visible ? cal.color : undefined }}
                >
                  {visible && (
                    <Check size={10} color="#fff" strokeWidth={3} />
                  )}
                </div>
                <span className={styles.calendarName}>{cal.name}</span>
              </div>
            );
          })}
        </div>

        <button
          className={styles.addButton}
          onClick={() => setShowCreateModal(true)}
        >
          <Plus size={16} />
          새 캘린더
        </button>
      </div>

      {/* 미니 달력 */}
      <div className={styles.miniCalendar}>
        <div className={styles.miniHeader}>
          <span className={styles.miniTitle}>
            {format(miniDate, 'yyyy년 M월', { locale: ko })}
          </span>
          <div className={styles.miniNavGroup}>
            <button
              className={styles.miniNav}
              onClick={() => setMiniDate(subMonths(miniDate, 1))}
            >
              <ChevronLeft size={14} />
            </button>
            <button
              className={styles.miniNav}
              onClick={() => setMiniDate(addMonths(miniDate, 1))}
            >
              <ChevronRight size={14} />
            </button>
          </div>
        </div>

        <div className={styles.miniGrid}>
          {WEEKDAY_LABELS.map((d) => (
            <div key={d} className={styles.miniDayHeader}>
              {d}
            </div>
          ))}
          {miniDays.map((day) => {
            const sameMonth = isSameMonth(day, miniDate);
            const today = isToday(day);
            const selected = isSameDay(day, currentDate);

            const cls = [
              styles.miniDay,
              !sameMonth && styles.miniDayOther,
              today && !selected && styles.miniDayToday,
              selected && styles.miniDaySelected,
            ]
              .filter(Boolean)
              .join(' ');

            return (
              <button
                key={day.toISOString()}
                className={cls}
                onClick={() => handleMiniDayClick(day)}
              >
                {format(day, 'd')}
              </button>
            );
          })}
        </div>
      </div>

      {/* 프로필 */}
      <div className={styles.profile}>
        <div className={styles.avatar}>{nickname.charAt(0)}</div>
        <span className={styles.profileName}>{nickname}</span>
        <button className={styles.settingsButton}>
          <Settings size={16} />
        </button>
      </div>

      {/* 캘린더 생성 모달 */}
      {showCreateModal && (
        <CreateCalendarModal onClose={() => setShowCreateModal(false)} />
      )}
    </div>
  );
}
