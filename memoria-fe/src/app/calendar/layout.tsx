'use client';

import { useCalendarStore } from '@/store/calendarStore';
import Sidebar from '@/components/calendar/Sidebar/Sidebar';
import CalendarHeader from '@/components/calendar/Header/CalendarHeader';
import styles from './layout.module.css';

export default function CalendarLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const sidebarOpen = useCalendarStore((s) => s.sidebarOpen);
  const setSidebarOpen = useCalendarStore((s) => s.setSidebarOpen);

  return (
    <div className={styles.wrapper}>
      {/* 모바일 오버레이 */}
      <div
        className={`${styles.overlay} ${!sidebarOpen ? styles.overlayHidden : ''}`}
        onClick={() => setSidebarOpen(false)}
      />

      {/* 사이드바 */}
      <aside
        className={[
          styles.sidebar,
          !sidebarOpen ? styles.sidebarHidden : '',
          sidebarOpen ? styles.sidebarVisible : '',
        ]
          .filter(Boolean)
          .join(' ')}
      >
        <Sidebar />
      </aside>

      {/* 메인 */}
      <div className={styles.main}>
        <CalendarHeader />
        <div className={styles.content}>{children}</div>
      </div>
    </div>
  );
}
