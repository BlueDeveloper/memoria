'use client';

import { useState, useMemo } from 'react';
import { useRouter } from 'next/navigation';
import {
  format,
  isToday,
  isTomorrow,
  parseISO,
  isBefore,
  addDays,
  startOfDay,
} from 'date-fns';
import { ko } from 'date-fns/locale';
import { Check, Plus, LogIn, LogOut, Clock, CalendarDays } from 'lucide-react';
import { useDiaryStore } from '@/store/diaryStore';
import { useAuthStore } from '@/store/authStore';
import api from '@/lib/api';
import { DiaryEvent } from '@/types/diary';
import AuthPromptModal from '@/components/diary/AuthPromptModal/AuthPromptModal';
import CreateDiaryModal from '@/components/diary/CreateDiaryModal/CreateDiaryModal';
import styles from './Sidebar.module.css';

export default function Sidebar() {
  const router = useRouter();
  const diaries = useDiaryStore((s) => s.diaries);
  const selectedDiaryId = useDiaryStore((s) => s.selectedDiaryId);
  const events = useDiaryStore((s) => s.events);
  const user = useAuthStore((s) => s.user);

  const [showAuthPrompt, setShowAuthPrompt] = useState(false);
  const [showCreateDiary, setShowCreateDiary] = useState(false);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const clearUser = useAuthStore((s) => s.clearUser);

  const currentDiary = diaries.find((d) => d.diaryId === selectedDiaryId);

  // 오늘의 일정
  const todayEvents = useMemo(() => {
    const now = new Date();
    return events.filter((e) => {
      const start = parseISO(e.startDt);
      return isToday(start) || (e.allDay && isToday(start));
    }).sort((a, b) => a.startDt.localeCompare(b.startDt));
  }, [events]);

  // 다가오는 일정 (내일~7일)
  const upcomingEvents = useMemo(() => {
    const tomorrow = startOfDay(addDays(new Date(), 1));
    const weekLater = startOfDay(addDays(new Date(), 8));
    return events.filter((e) => {
      const start = parseISO(e.startDt);
      return start >= tomorrow && isBefore(start, weekLater);
    }).sort((a, b) => a.startDt.localeCompare(b.startDt))
      .slice(0, 5);
  }, [events]);

  const formatEventTime = (event: DiaryEvent): string => {
    if (event.allDay) return '종일';
    return format(parseISO(event.startDt), 'HH:mm');
  };

  const formatEventDate = (event: DiaryEvent): string => {
    const start = parseISO(event.startDt);
    if (isTomorrow(start)) return '내일';
    return format(start, 'M/d (EEE)', { locale: ko });
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
      {isAuthenticated ? (
        <button className={styles.backButton} onClick={() => setShowCreateDiary(true)}>
          <Plus size={16} />
          새 다이어리 만들기
        </button>
      ) : (
        <button className={styles.backButton} onClick={() => setShowAuthPrompt(true)}>
          <Plus size={16} />
          새 다이어리 만들기
        </button>
      )}

      {/* 오늘의 일정 */}
      <div className={styles.eventSection}>
        <div className={styles.eventSectionTitle}>
          <Clock size={14} />
          오늘의 일정
        </div>
        {todayEvents.length === 0 ? (
          <div className={styles.emptyState}>오늘 일정이 없습니다</div>
        ) : (
          <div className={styles.eventList}>
            {todayEvents.map((event) => (
              <div key={event.eventId} className={styles.eventItem}>
                <div
                  className={styles.eventDot}
                  style={{ backgroundColor: event.color || currentDiary?.color || 'var(--color-primary)' }}
                />
                <div className={styles.eventInfo}>
                  <span className={styles.eventTitle}>{event.title}</span>
                  <span className={styles.eventTime}>{formatEventTime(event)}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 다가오는 일정 */}
      <div className={styles.eventSection}>
        <div className={styles.eventSectionTitle}>
          <CalendarDays size={14} />
          다가오는 일정
        </div>
        {upcomingEvents.length === 0 ? (
          <div className={styles.emptyState}>예정된 일정이 없습니다</div>
        ) : (
          <div className={styles.eventList}>
            {upcomingEvents.map((event) => (
              <div key={event.eventId} className={styles.eventItem}>
                <div
                  className={styles.eventDot}
                  style={{ backgroundColor: event.color || currentDiary?.color || 'var(--color-primary)' }}
                />
                <div className={styles.eventInfo}>
                  <span className={styles.eventTitle}>{event.title}</span>
                  <span className={styles.eventTime}>
                    {formatEventDate(event)} {!event.allDay && formatEventTime(event)}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 프로필 (로그인 시만) */}
      {isAuthenticated && (
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
