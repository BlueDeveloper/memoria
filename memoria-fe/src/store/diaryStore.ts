import { create } from 'zustand';
import {
  addMonths,
  subMonths,
  addWeeks,
  subWeeks,
} from 'date-fns';
import { Diary, DiaryEvent, ViewMode } from '@/types/diary';

interface DiaryState {
  diaries: Diary[];
  selectedDiaryId: number | null;
  events: DiaryEvent[];
  currentDate: Date;
  viewMode: ViewMode;
  visibleDiaryIds: Set<number>;
  sidebarOpen: boolean;

  setDiaries: (diaries: Diary[]) => void;
  selectDiary: (id: number | null) => void;
  setEvents: (events: DiaryEvent[]) => void;
  setCurrentDate: (date: Date) => void;
  setViewMode: (mode: ViewMode) => void;
  navigateMonth: (direction: 1 | -1) => void;
  navigateWeek: (direction: 1 | -1) => void;
  toggleDiaryVisibility: (id: number) => void;
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
}

export const useDiaryStore = create<DiaryState>((set) => ({
  diaries: [],
  selectedDiaryId: null,
  events: [],
  currentDate: new Date(),
  viewMode: 'month',
  visibleDiaryIds: new Set<number>(),
  sidebarOpen: true,

  setDiaries: (diaries) =>
    set((state) => ({
      diaries,
      visibleDiaryIds: new Set([
        ...state.visibleDiaryIds,
        ...diaries.map((d) => d.diaryId),
      ]),
    })),

  selectDiary: (id) => set({ selectedDiaryId: id }),

  setEvents: (events) => set({ events }),

  setCurrentDate: (date) => set({ currentDate: date }),

  setViewMode: (mode) => set({ viewMode: mode }),

  navigateMonth: (direction) =>
    set((state) => ({
      currentDate:
        direction === 1
          ? addMonths(state.currentDate, 1)
          : subMonths(state.currentDate, 1),
    })),

  navigateWeek: (direction) =>
    set((state) => ({
      currentDate:
        direction === 1
          ? addWeeks(state.currentDate, 1)
          : subWeeks(state.currentDate, 1),
    })),

  toggleDiaryVisibility: (id) =>
    set((state) => {
      const next = new Set(state.visibleDiaryIds);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return { visibleDiaryIds: next };
    }),

  toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),

  setSidebarOpen: (open) => set({ sidebarOpen: open }),
}));
