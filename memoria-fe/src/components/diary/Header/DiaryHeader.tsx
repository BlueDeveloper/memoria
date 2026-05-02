'use client';

import { format, startOfWeek, endOfWeek } from 'date-fns';
import { ko } from 'date-fns/locale';
import { useState, useRef, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { ChevronLeft, ChevronRight, Bell, Menu, LogIn, Home, LogOut, User } from 'lucide-react';
import api from '@/lib/api';
import { useDiaryStore } from '@/store/diaryStore';
import { useAuthStore } from '@/store/authStore';
import AuthPromptModal from '@/components/diary/AuthPromptModal/AuthPromptModal';
import styles from './DiaryHeader.module.css';

export default function DiaryHeader() {
  const currentDate = useDiaryStore((s) => s.currentDate);
  const viewMode = useDiaryStore((s) => s.viewMode);
  const setViewMode = useDiaryStore((s) => s.setViewMode);
  const setCurrentDate = useDiaryStore((s) => s.setCurrentDate);
  const navigateMonth = useDiaryStore((s) => s.navigateMonth);
  const navigateWeek = useDiaryStore((s) => s.navigateWeek);
  const toggleSidebar = useDiaryStore((s) => s.toggleSidebar);
  const user = useAuthStore((s) => s.user);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const clearUser = useAuthStore((s) => s.clearUser);
  const [showAuthPrompt, setShowAuthPrompt] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const profileMenuRef = useRef<HTMLDivElement>(null);
  const router = useRouter();

  // 프로필 메뉴 외부 클릭 시 닫기
  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (profileMenuRef.current && !profileMenuRef.current.contains(e.target as Node)) {
        setShowProfileMenu(false);
      }
    };
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  const handleLogout = async () => {
    try { await api.post('/api/auth/logout'); } catch {}
    clearUser();
    setShowProfileMenu(false);
    router.push('/');
  };

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
        <button className={styles.menuButton} onClick={() => router.push('/')} title="인트로">
          <Home size={18} />
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
        {isAuthenticated ? (
          <>
            <div className={styles.profileWrapper} ref={profileMenuRef}>
              <button
                className={styles.profileAvatar}
                onClick={() => setShowProfileMenu(!showProfileMenu)}
              >
                {nickname.charAt(0)}
              </button>
              {showProfileMenu && (
                <div className={styles.profileMenu}>
                  <div className={styles.profileMenuHeader}>
                    <span className={styles.profileMenuName}>{nickname}</span>
                    <span className={styles.profileMenuEmail}>{user?.email}</span>
                  </div>
                  <div className={styles.profileMenuDivider} />
                  <button className={styles.profileMenuItem} onClick={handleLogout}>
                    <LogOut size={14} />
                    로그아웃
                  </button>
                </div>
              )}
            </div>
          </>
        ) : (
          <button
            className={styles.loginButton}
            onClick={() => setShowAuthPrompt(true)}
          >
            <LogIn size={16} />
            로그인
          </button>
        )}
      </div>

      {showAuthPrompt && (
        <AuthPromptModal onClose={() => setShowAuthPrompt(false)} />
      )}
    </header>
  );
}
