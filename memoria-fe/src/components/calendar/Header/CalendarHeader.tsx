'use client';

import { format, startOfWeek, endOfWeek } from 'date-fns';
import { ko } from 'date-fns/locale';
import { ChevronLeft, ChevronRight, Bell, Menu } from 'lucide-react';
import { useCalendarStore } from '@/store/calendarStore';
import { useAuthStore } from '@/store/authStore';
import styles from './CalendarHeader.module.css';

export default function CalendarHeader() {
  const currentDate = useCalendarStore((s) => s.currentDate);
  const viewMode = useCalendarStore((s) => s.viewMode);
  const setViewMode = useCalendarStore((s) => s.setViewMode);
  const setCurrentDate = useCalendarStore((s) => s.setCurrentDate);
  const navigateMonth = useCalendarStore((s) => s.navigateMonth);
  const navigateWeek = useCalendarStore((s) => s.navigateWeek);
  const toggleSidebar = useCalendarStore((s) => s.toggleSidebar);
  const user = useAuthStore((s) => s.user);

  const handlePrev = () => {
    if (viewMode === 'month') {
      navigateMonth(-1);
    } else {
      navigateWeek(-1);
    }
  };

  const handleNext = () => {
    if (viewMode === 'month') {
      navigateMonth(1);
    } else {
      navigateWeek(1);
    }
  };

  const handleToday = () => {
    setCurrentDate(new Date());
  };

  const getPeriodLabel = (): string => {
    if (viewMode === 'month') {
      return format(currentDate, 'yyyy년 M월', { locale: ko });
    }
    const weekStart = startOfWeek(currentDate, { weekStartsOn: 0 });
    const weekEnd = endOfWeek(currentDate, { weekStartsOn: 0 });
    const startLabel = format(weekStart, 'M월 d일', { locale: ko });
    const endLabel = format(weekEnd, 'M월 d일', { locale: ko });
    return `${startLabel} - ${endLabel}`;
  };

  const nickname = user?.nickname ?? '사용자';

  return (
    <header className={styles.header}>
      <div className={styles.left}>
        <button className={styles.menuButton} onClick={toggleSidebar}>
          <Menu size={20} />
        </button>
        <button className={styles.todayButton} onClick={handleToday}>
          오늘
        </button>
        <div className={styles.navGroup}>
          <button className={styles.navButton} onClick={handlePrev}>
            <ChevronLeft size={18} />
          </button>
          <button className={styles.navButton} onClick={handleNext}>
            <ChevronRight size={18} />
          </button>
        </div>
        <span className={styles.currentPeriod}>{getPeriodLabel()}</span>
      </div>

      <div className={styles.right}>
        <div className={styles.viewToggle}>
          <button
            className={`${styles.viewButton} ${viewMode === 'month' ? styles.viewButtonActive : ''}`}
            onClick={() => setViewMode('month')}
          >
            월간
          </button>
          <button
            className={`${styles.viewButton} ${viewMode === 'week' ? styles.viewButtonActive : ''}`}
            onClick={() => setViewMode('week')}
          >
            주간
          </button>
        </div>
        <button className={styles.iconButton}>
          <Bell size={18} />
          <span className={styles.badge} />
        </button>
        <div className={styles.profileAvatar}>{nickname.charAt(0)}</div>
      </div>
    </header>
  );
}
