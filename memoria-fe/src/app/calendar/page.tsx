'use client';

import { useEffect } from 'react';
import { useCalendarStore } from '@/store/calendarStore';
import MonthView from '@/components/calendar/MonthView/MonthView';
import WeekView from '@/components/calendar/WeekView/WeekView';
import { Calendar, CalendarEvent } from '@/types/calendar';

// 목업 데이터 (API 미연결 상태에서 UI 확인용)
const MOCK_CALENDARS: Calendar[] = [
  {
    calendarId: 1,
    name: '우리 커플',
    color: '#E91E63',
    inviteCode: 'abc123',
    ownerNickname: '나',
    memberCount: 2,
    myRole: 'OWNER',
  },
  {
    calendarId: 2,
    name: '가족 일정',
    color: '#4A90D9',
    inviteCode: 'def456',
    ownerNickname: '나',
    memberCount: 4,
    myRole: 'OWNER',
  },
  {
    calendarId: 3,
    name: '팀 프로젝트',
    color: '#27AE60',
    inviteCode: 'ghi789',
    ownerNickname: '팀장',
    memberCount: 5,
    myRole: 'MEMBER',
  },
];

const today = new Date();
const y = today.getFullYear();
const m = today.getMonth();
const d = today.getDate();

const MOCK_EVENTS: CalendarEvent[] = [
  {
    eventId: 1,
    calendarId: 1,
    title: '데이트',
    startDt: new Date(y, m, d, 18, 0).toISOString(),
    endDt: new Date(y, m, d, 20, 0).toISOString(),
    allDay: false,
    repeatType: 'NONE',
    creatorNickname: '나',
  },
  {
    eventId: 2,
    calendarId: 2,
    title: '가족 저녁',
    startDt: new Date(y, m, d + 1, 19, 0).toISOString(),
    endDt: new Date(y, m, d + 1, 21, 0).toISOString(),
    allDay: false,
    repeatType: 'NONE',
    creatorNickname: '엄마',
  },
  {
    eventId: 3,
    calendarId: 3,
    title: '팀 회의',
    startDt: new Date(y, m, d, 10, 0).toISOString(),
    endDt: new Date(y, m, d, 11, 30).toISOString(),
    allDay: false,
    repeatType: 'WEEKLY',
    creatorNickname: '팀장',
    color: '#27AE60',
  },
  {
    eventId: 4,
    calendarId: 1,
    title: '기념일',
    startDt: new Date(y, m, d + 3).toISOString(),
    endDt: new Date(y, m, d + 3).toISOString(),
    allDay: true,
    repeatType: 'YEARLY',
    creatorNickname: '나',
  },
  {
    eventId: 5,
    calendarId: 2,
    title: '병원 예약',
    startDt: new Date(y, m, d + 2, 14, 0).toISOString(),
    endDt: new Date(y, m, d + 2, 15, 0).toISOString(),
    allDay: false,
    repeatType: 'NONE',
    creatorNickname: '아빠',
    location: '서울대병원',
  },
];

export default function CalendarPage() {
  const viewMode = useCalendarStore((s) => s.viewMode);
  const setCalendars = useCalendarStore((s) => s.setCalendars);
  const setEvents = useCalendarStore((s) => s.setEvents);

  useEffect(() => {
    // TODO: API 연동 시 교체
    setCalendars(MOCK_CALENDARS);
    setEvents(MOCK_EVENTS);
  }, [setCalendars, setEvents]);

  return viewMode === 'month' ? <MonthView /> : <WeekView />;
}
