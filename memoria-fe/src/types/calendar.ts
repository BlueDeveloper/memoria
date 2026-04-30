export type CalendarRole = 'OWNER' | 'ADMIN' | 'MEMBER';
export type RepeatType = 'NONE' | 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
export type ViewMode = 'month' | 'week';

export interface Calendar {
  calendarId: number;
  name: string;
  description?: string;
  color: string;
  inviteCode: string;
  ownerNickname: string;
  memberCount: number;
  myRole: CalendarRole;
}

export interface CalendarEvent {
  eventId: number;
  calendarId: number;
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

export interface CalendarMember {
  memberId: number;
  nickname: string;
  profileImage?: string;
  color: string;
  role: CalendarRole;
}

export interface CreateCalendarRequest {
  name: string;
  description?: string;
  color: string;
  groupType?: string;
}

export interface UpdateCalendarRequest {
  name?: string;
  description?: string;
  color?: string;
}

export interface CreateEventRequest {
  calendarId: number;
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
