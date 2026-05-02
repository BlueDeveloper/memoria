'use client';

import DiaryHeader from '@/components/diary/Header/DiaryHeader';
import styles from './layout.module.css';

export default function DiaryLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className={styles.wrapper}>
      <div className={styles.main}>
        <DiaryHeader />
        <div className={styles.content}>{children}</div>
      </div>
    </div>
  );
}
