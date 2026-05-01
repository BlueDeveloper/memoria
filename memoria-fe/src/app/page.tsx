'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { Plus, LogIn } from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { useDiaryStore } from '@/store/diaryStore';
import { getMyDiaries } from '@/lib/diaryApi';
import { Diary } from '@/types/diary';
import CreateDiaryModal from '@/components/diary/CreateDiaryModal/CreateDiaryModal';
import AuthPromptModal from '@/components/diary/AuthPromptModal/AuthPromptModal';
import styles from './page.module.css';

// 비로그인 시 샘플 다이어리 1개 (바로 둘러보기 진입)
const SAMPLE_DIARY: Diary = {
  diaryId: -1,
  name: '나의 다이어리',
  color: '#1B2A4A',
  diaryType: 'GENERAL',
  logoId: null,
  themeId: null,
  inviteCode: 'sample',
  ownerNickname: '샘플',
  memberCount: 1,
  myRole: 'OWNER',
};

const DIARY_TYPE_EMOJI: Record<string, string> = {
  GENERAL: '📔',
  COUPLE: '💑',
  FAMILY: '👨‍👩‍👧‍👦',
  TEAM: '👥',
  WEDDING: '💒',
};

function getGreeting(): string {
  const hour = new Date().getHours();
  if (hour < 6) return '편안한 밤 되세요';
  if (hour < 12) return '좋은 아침이에요';
  if (hour < 18) return '오늘도 좋은 하루 보내세요';
  return '오늘 하루도 수고했어요';
}

export default function IntroPage() {
  const router = useRouter();
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const user = useAuthStore((s) => s.user);
  const setDiaries = useDiaryStore((s) => s.setDiaries);

  const [diaries, setLocalDiaries] = useState<Diary[]>([]);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showAuthPrompt, setShowAuthPrompt] = useState(false);
  const [loaded, setLoaded] = useState(false);

  const fetchDiaries = useCallback(async () => {
    if (!isAuthenticated) {
      // 비로그인: 샘플 1개로 바로 다이어리 진입
      setLocalDiaries([SAMPLE_DIARY]);
      setDiaries([SAMPLE_DIARY]);
      setLoaded(true);
      return;
    }
    try {
      const data = await getMyDiaries();
      setLocalDiaries(data);
      setDiaries(data);
    } catch {
      setLocalDiaries([SAMPLE_DIARY]);
      setDiaries([SAMPLE_DIARY]);
    }
    setLoaded(true);
  }, [isAuthenticated, setDiaries]);

  useEffect(() => {
    fetchDiaries();
  }, [fetchDiaries]);

  // 다이어리 1개면 인트로 스킵 → 바로 진입
  useEffect(() => {
    if (loaded && diaries.length === 1) {
      router.replace(`/diary?id=${diaries[0].diaryId}`);
    }
  }, [loaded, diaries, router]);

  const handleDiaryClick = (diary: Diary) => {
    router.push(`/diary?id=${diary.diaryId}`);
  };

  const handleAddClick = () => {
    if (!isAuthenticated) {
      setShowAuthPrompt(true);
      return;
    }
    setShowCreateModal(true);
  };

  const handleLogin = () => {
    router.push('/auth/login');
  };

  const todayLabel = format(new Date(), 'M월 d일 EEEE', { locale: ko });
  const nickname = user?.nickname ?? '사용자';

  // 로딩 중이거나 다이어리 1개 이하면 인트로 렌더링하지 않음 (리다이렉트 처리)
  if (!loaded || diaries.length <= 1) {
    return null;
  }

  return (
    <div className={styles.container}>
      {/* 상단 헤더 */}
      <header className={styles.header}>
        <div className={styles.brandArea}>
          <img src="/logo.png" alt="Memoria" className={styles.brandLogo} />
          <div className={styles.dateInfo}>
            <span className={styles.todayDate}>{todayLabel}</span>
            <span className={styles.greeting}>{getGreeting()}</span>
          </div>
        </div>
      </header>

      {/* 다이어리 카드 그리드 */}
      <main className={styles.main}>
        <div className={styles.grid}>
          {diaries.map((diary) => (
            <button
              key={diary.diaryId}
              className={styles.card}
              onClick={() => handleDiaryClick(diary)}
            >
              <div
                className={styles.cardLogo}
                style={{ backgroundColor: diary.color }}
              >
                <span className={styles.cardEmoji}>
                  {DIARY_TYPE_EMOJI[diary.diaryType] ?? '📔'}
                </span>
              </div>
              <span className={styles.cardName}>{diary.name}</span>
              <span className={styles.cardBadge}>
                {diary.memberCount}명
              </span>
            </button>
          ))}

          {/* 새 다이어리 추가 카드 */}
          <button className={styles.addCard} onClick={handleAddClick}>
            <div className={styles.addIcon}>
              <Plus size={28} strokeWidth={1.5} />
            </div>
            <span className={styles.addLabel}>새 다이어리</span>
          </button>
        </div>
      </main>

      {/* 하단 프로필 영역 */}
      <footer className={styles.footer}>
        {isAuthenticated ? (
          <div className={styles.profileArea}>
            <div className={styles.profileAvatar}>
              {nickname.charAt(0)}
            </div>
            <span className={styles.profileName}>{nickname}</span>
          </div>
        ) : (
          <button className={styles.loginButton} onClick={handleLogin}>
            <LogIn size={16} />
            로그인
          </button>
        )}
      </footer>

      {/* 모달 */}
      {showCreateModal && (
        <CreateDiaryModal
          onClose={() => setShowCreateModal(false)}
          onCreated={() => fetchDiaries()}
        />
      )}
      {showAuthPrompt && (
        <AuthPromptModal onClose={() => setShowAuthPrompt(false)} />
      )}
    </div>
  );
}
