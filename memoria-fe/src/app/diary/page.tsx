'use client';

import { useEffect, useCallback, Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import {
  format,
  startOfMonth,
  endOfMonth,
  startOfWeek,
  endOfWeek,
} from 'date-fns';
import { useDiaryStore } from '@/store/diaryStore';
import { useAuthStore } from '@/store/authStore';
import MonthView from '@/components/diary/MonthView/MonthView';
import WeekView from '@/components/diary/WeekView/WeekView';
import { getMyDiaries, getEvents } from '@/lib/diaryApi';
import { Diary, DiaryEvent } from '@/types/diary';

const today = new Date();
const y = today.getFullYear();
const m = today.getMonth();
const d = today.getDate();

const MOCK_DIARIES: Diary[] = [
  { diaryId: 1, name: '우리 커플', color: '#E91E63', diaryType: 'COUPLE', logoId: null, themeId: null, inviteCode: 'abc123', ownerNickname: '나', memberCount: 2, myRole: 'OWNER' },
  { diaryId: 2, name: '가족 일정', color: '#4A90D9', diaryType: 'FAMILY', logoId: null, themeId: null, inviteCode: 'def456', ownerNickname: '나', memberCount: 4, myRole: 'OWNER' },
  { diaryId: 3, name: '팀 프로젝트', color: '#27AE60', diaryType: 'TEAM', logoId: null, themeId: null, inviteCode: 'ghi789', ownerNickname: '팀장', memberCount: 5, myRole: 'MEMBER' },
];

const MOCK_EVENTS: DiaryEvent[] = [
  { eventId: 1, diaryId: 1, title: '데이트', startDt: new Date(y, m, d, 18, 0).toISOString(), endDt: new Date(y, m, d, 20, 0).toISOString(), allDay: false, repeatType: 'NONE', creatorNickname: '나' },
  { eventId: 2, diaryId: 2, title: '가족 저녁', startDt: new Date(y, m, d + 1, 19, 0).toISOString(), endDt: new Date(y, m, d + 1, 21, 0).toISOString(), allDay: false, repeatType: 'NONE', creatorNickname: '엄마' },
  { eventId: 3, diaryId: 3, title: '팀 회의', startDt: new Date(y, m, d, 10, 0).toISOString(), endDt: new Date(y, m, d, 11, 30).toISOString(), allDay: false, repeatType: 'WEEKLY', creatorNickname: '팀장', color: '#27AE60' },
  { eventId: 4, diaryId: 1, title: '기념일', startDt: new Date(y, m, d + 3).toISOString(), endDt: new Date(y, m, d + 3).toISOString(), allDay: true, repeatType: 'YEARLY', creatorNickname: '나' },
  { eventId: 5, diaryId: 2, title: '병원 예약', startDt: new Date(y, m, d + 2, 14, 0).toISOString(), endDt: new Date(y, m, d + 2, 15, 0).toISOString(), allDay: false, repeatType: 'NONE', creatorNickname: '아빠', location: '서울대병원' },
];

function DiaryContent() {
  const searchParams = useSearchParams();
  const diaryId = Number(searchParams.get('id') ?? 1);

  const viewMode = useDiaryStore((s) => s.viewMode);
  const currentDate = useDiaryStore((s) => s.currentDate);
  const diaries = useDiaryStore((s) => s.diaries);
  const setDiaries = useDiaryStore((s) => s.setDiaries);
  const setEvents = useDiaryStore((s) => s.setEvents);
  const selectDiary = useDiaryStore((s) => s.selectDiary);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  useEffect(() => {
    selectDiary(diaryId);
  }, [diaryId, selectDiary]);

  const fetchDiaries = useCallback(async () => {
    // 로그인 상태면 실제 다이어리, 아니면 목업
    if (!isAuthenticated) {
      if (diaries.length === 0 || diaries[0]?.diaryId < 0) {
        setDiaries(MOCK_DIARIES);
      }
      return;
    }
    try {
      const data = await getMyDiaries();
      setDiaries(data);
      // 현재 선택된 다이어리 ID 업데이트
      if (data.length > 0 && diaryId <= 0) {
        selectDiary(data[0].diaryId);
      }
    } catch {
      setDiaries(MOCK_DIARIES);
    }
  }, [isAuthenticated, diaries, diaryId, setDiaries, selectDiary]);

  const fetchEvents = useCallback(async () => {
    const viewStart =
      viewMode === 'month'
        ? startOfWeek(startOfMonth(currentDate), { weekStartsOn: 0 })
        : startOfWeek(currentDate, { weekStartsOn: 0 });
    const viewEnd =
      viewMode === 'month'
        ? endOfWeek(endOfMonth(currentDate), { weekStartsOn: 0 })
        : endOfWeek(currentDate, { weekStartsOn: 0 });

    const start = format(viewStart, "yyyy-MM-dd'T'HH:mm:ss");
    const end = format(viewEnd, "yyyy-MM-dd'T'HH:mm:ss");

    try {
      const events = await getEvents(diaryId, start, end);
      setEvents(events);
    } catch {
      setEvents(MOCK_EVENTS.filter((e) => e.diaryId === diaryId));
    }
  }, [diaryId, currentDate, viewMode, setEvents]);

  useEffect(() => {
    fetchDiaries();
  }, [fetchDiaries]);

  useEffect(() => {
    fetchEvents();
  }, [fetchEvents]);

  return viewMode === 'month' ? <MonthView /> : <WeekView />;
}

export default function DiaryPage() {
  return (
    <Suspense fallback={<div style={{ padding: 40, textAlign: 'center' }}>로딩 중...</div>}>
      <DiaryContent />
    </Suspense>
  );
}
