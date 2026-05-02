import api from '@/lib/api';
import { ApiResponse } from '@/types/api';
import {
  Diary,
  DiaryEvent,
  DiaryMember,
  CreateDiaryRequest,
  UpdateDiaryRequest,
  CreateEventRequest,
  UpdateEventRequest,
} from '@/types/diary';

const BASE = '/api/diaries';

/* ---------- 다이어리 ---------- */

export async function getMyDiaries(): Promise<Diary[]> {
  const { data } = await api.get<ApiResponse<Diary[]>>(BASE);
  return data.data;
}

export async function getDiaryDetail(id: number): Promise<Diary> {
  const { data } = await api.get<ApiResponse<Diary>>(`${BASE}/${id}`);
  return data.data;
}

export async function createDiary(
  req: CreateDiaryRequest,
): Promise<Diary> {
  const { data } = await api.post<ApiResponse<Diary>>(BASE, req);
  return data.data;
}

export async function updateDiary(
  id: number,
  req: UpdateDiaryRequest,
): Promise<Diary> {
  const { data } = await api.put<ApiResponse<Diary>>(`${BASE}/${id}`, req);
  return data.data;
}

export async function deleteDiary(id: number): Promise<void> {
  await api.delete(`${BASE}/${id}`);
}

/* ---------- 이벤트 ---------- */

export async function getEvents(
  diaryId: number,
  start: string,
  end: string,
): Promise<DiaryEvent[]> {
  const { data } = await api.get<ApiResponse<DiaryEvent[]>>(
    `${BASE}/${diaryId}/events`,
    { params: { start, end } },
  );
  return data.data;
}

export async function createEvent(
  req: CreateEventRequest,
): Promise<DiaryEvent> {
  const { data } = await api.post<ApiResponse<DiaryEvent>>(
    '/api/events',
    req,
  );
  return data.data;
}

export async function updateEvent(
  id: number,
  req: UpdateEventRequest,
): Promise<DiaryEvent> {
  const { data } = await api.put<ApiResponse<DiaryEvent>>(
    `/api/events/${id}`,
    req,
  );
  return data.data;
}

export async function deleteEvent(id: number): Promise<void> {
  await api.delete(`/api/events/${id}`);
}

/* ---------- 멤버 ---------- */

export async function getDiaryMembers(
  diaryId: number,
): Promise<DiaryMember[]> {
  const { data } = await api.get<ApiResponse<DiaryMember[]>>(
    `${BASE}/${diaryId}/members`,
  );
  return data.data;
}
