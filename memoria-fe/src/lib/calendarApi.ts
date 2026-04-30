import api from '@/lib/api';
import { ApiResponse } from '@/types/api';
import {
  Calendar,
  CalendarEvent,
  CalendarMember,
  CreateCalendarRequest,
  UpdateCalendarRequest,
  CreateEventRequest,
  UpdateEventRequest,
} from '@/types/calendar';

const BASE = '/api/calendars';

/* ---------- 캘린더 ---------- */

export async function getMyCalendars(): Promise<Calendar[]> {
  const { data } = await api.get<ApiResponse<Calendar[]>>(BASE);
  return data.data;
}

export async function getCalendarDetail(id: number): Promise<Calendar> {
  const { data } = await api.get<ApiResponse<Calendar>>(`${BASE}/${id}`);
  return data.data;
}

export async function createCalendar(
  req: CreateCalendarRequest,
): Promise<Calendar> {
  const { data } = await api.post<ApiResponse<Calendar>>(BASE, req);
  return data.data;
}

export async function updateCalendar(
  id: number,
  req: UpdateCalendarRequest,
): Promise<Calendar> {
  const { data } = await api.put<ApiResponse<Calendar>>(`${BASE}/${id}`, req);
  return data.data;
}

export async function deleteCalendar(id: number): Promise<void> {
  await api.delete(`${BASE}/${id}`);
}

/* ---------- 이벤트 ---------- */

export async function getEvents(
  calendarId: number,
  start: string,
  end: string,
): Promise<CalendarEvent[]> {
  const { data } = await api.get<ApiResponse<CalendarEvent[]>>(
    `${BASE}/${calendarId}/events`,
    { params: { start, end } },
  );
  return data.data;
}

export async function createEvent(
  req: CreateEventRequest,
): Promise<CalendarEvent> {
  const { data } = await api.post<ApiResponse<CalendarEvent>>(
    `${BASE}/${req.calendarId}/events`,
    req,
  );
  return data.data;
}

export async function updateEvent(
  id: number,
  req: UpdateEventRequest,
): Promise<CalendarEvent> {
  const { data } = await api.put<ApiResponse<CalendarEvent>>(
    `${BASE}/events/${id}`,
    req,
  );
  return data.data;
}

export async function deleteEvent(id: number): Promise<void> {
  await api.delete(`${BASE}/events/${id}`);
}

/* ---------- 멤버 ---------- */

export async function getCalendarMembers(
  calendarId: number,
): Promise<CalendarMember[]> {
  const { data } = await api.get<ApiResponse<CalendarMember[]>>(
    `${BASE}/${calendarId}/members`,
  );
  return data.data;
}
