export type DiaryRole = 'OWNER' | 'ADMIN' | 'MEMBER';
export type DiaryType = 'GENERAL' | 'COUPLE' | 'FAMILY' | 'TEAM' | 'WEDDING';
export type RepeatType = 'NONE' | 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
export type ViewMode = 'month' | 'week';

export interface Diary {
  diaryId: number;
  name: string;
  description?: string;
  color: string;
  diaryType: DiaryType;
  logoId: number | null;
  themeId: number | null;
  inviteCode: string;
  ownerNickname: string;
  memberCount: number;
  myRole: DiaryRole;
}

export interface DiaryEvent {
  eventId: number;
  diaryId: number;
  title: string;
  description?: string;
  location?: string;
  startDt: string;
  endDt: string;
  allDay: boolean;
  color?: string;
  repeatType: RepeatType;
  remindMinutes?: number;
  creatorNickname: string;
}

export interface DiaryMember {
  memberId: number;
  nickname: string;
  profileImage?: string;
  color: string;
  role: DiaryRole;
}

export interface CreateDiaryRequest {
  name: string;
  description?: string;
  color: string;
  diaryType?: string;
}

export interface UpdateDiaryRequest {
  name?: string;
  description?: string;
  color?: string;
}

export interface CreateEventRequest {
  diaryId: number;
  title: string;
  description?: string;
  location?: string;
  startDt: string;
  endDt: string;
  allDay: boolean;
  color?: string;
  repeatType: RepeatType;
  remindMinutes?: number;
}

export interface UpdateEventRequest {
  title?: string;
  description?: string;
  location?: string;
  startDt?: string;
  endDt?: string;
  allDay?: boolean;
  color?: string;
  repeatType?: RepeatType;
  remindMinutes?: number;
}
