'use client';

import { useDiaryStore } from '@/store/diaryStore';
import Sidebar from '@/components/diary/Sidebar/Sidebar';
import DiaryHeader from '@/components/diary/Header/DiaryHeader';
import styles from './layout.module.css';

export default function DiaryLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const sidebarOpen = useDiaryStore((s) => s.sidebarOpen);
  const setSidebarOpen = useDiaryStore((s) => s.setSidebarOpen);

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
        <DiaryHeader />
        <div className={styles.content}>{children}</div>
      </div>
    </div>
  );
}
