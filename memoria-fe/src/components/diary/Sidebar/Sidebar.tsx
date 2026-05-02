'use client';

import { useState, useMemo } from 'react';
import { useRouter } from 'next/navigation';
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
import { Settings, ChevronLeft, ChevronRight, Check, Plus, LogIn, LogOut } from 'lucide-react';
import { useDiaryStore } from '@/store/diaryStore';
import { useAuthStore } from '@/store/authStore';
import api from '@/lib/api';
import AuthPromptModal from '@/components/diary/AuthPromptModal/AuthPromptModal';
import CreateDiaryModal from '@/components/diary/CreateDiaryModal/CreateDiaryModal';
import styles from './Sidebar.module.css';

const WEEKDAY_LABELS = ['일', '월', '화', '수', '목', '금', '토'];

export default function Sidebar() {
  const router = useRouter();
  const diaries = useDiaryStore((s) => s.diaries);
  const selectedDiaryId = useDiaryStore((s) => s.selectedDiaryId);
  const visibleDiaryIds = useDiaryStore((s) => s.visibleDiaryIds);
  const toggleDiaryVisibility = useDiaryStore((s) => s.toggleDiaryVisibility);
  const setCurrentDate = useDiaryStore((s) => s.setCurrentDate);
  const currentDate = useDiaryStore((s) => s.currentDate);
  const user = useAuthStore((s) => s.user);

  const [miniDate, setMiniDate] = useState(new Date());
  const [showAuthPrompt, setShowAuthPrompt] = useState(false);
  const [showCreateDiary, setShowCreateDiary] = useState(false);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const clearUser = useAuthStore((s) => s.clearUser);

  const currentDiary = diaries.find((d) => d.diaryId === selectedDiaryId);

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

  const handleBackToIntro = () => {
    if (!isAuthenticated) {
      setShowAuthPrompt(true);
      return;
    }
    router.push('/');
  };

  const nickname = user?.nickname ?? '사용자';

  return (
    <div className={styles.sidebar}>
      <div className={styles.logo}>
        <img src="/logo.png" alt="Memoria" className={styles.logoImage} />
      </div>

      {/* 현재 다이어리 정보 */}
      {currentDiary && (
        <div className={styles.section}>
          <div className={styles.sectionTitle}>현재 다이어리</div>
          <div className={styles.diaryList}>
            <div className={styles.diaryItem}>
              <div
                className={`${styles.checkbox} ${styles.checkboxChecked}`}
                style={{ backgroundColor: currentDiary.color }}
              >
                <Check size={10} color="#fff" strokeWidth={3} />
              </div>
              <span className={styles.diaryName}>{currentDiary.name}</span>
            </div>
          </div>
        </div>
      )}

      {/* 다이어리 관리 */}
      {isAuthenticated && (
        <>
          <button className={styles.backButton} onClick={() => setShowCreateDiary(true)}>
            <Plus size={16} />
            새 다이어리 만들기
          </button>
        </>
      )}
      {!isAuthenticated && (
        <button className={styles.backButton} onClick={() => setShowAuthPrompt(true)}>
          <Plus size={16} />
          새 다이어리 만들기
        </button>
      )}

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
      {isAuthenticated ? (
        <div className={styles.profile}>
          <div className={styles.avatar}>{nickname.charAt(0)}</div>
          <span className={styles.profileName}>{nickname}</span>
          <button
            className={styles.settingsButton}
            onClick={async () => {
              try { await api.post('/api/auth/logout'); } catch {}
              clearUser();
              router.push('/');
            }}
            title="로그아웃"
          >
            <LogOut size={16} />
          </button>
        </div>
      ) : (
        <button
          className={styles.loginProfile}
          onClick={() => setShowAuthPrompt(true)}
        >
          <LogIn size={16} />
          <span>로그인 / 회원가입</span>
        </button>
      )}

      {showAuthPrompt && (
        <AuthPromptModal onClose={() => setShowAuthPrompt(false)} />
      )}
      {showCreateDiary && (
        <CreateDiaryModal onClose={() => setShowCreateDiary(false)} />
      )}
    </div>
  );
}
