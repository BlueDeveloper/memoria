import { create } from 'zustand';
import {
  addMonths,
  subMonths,
  addWeeks,
  subWeeks,
} from 'date-fns';
import { Calendar, CalendarEvent, ViewMode } from '@/types/calendar';

interface CalendarState {
  calendars: Calendar[];
  selectedCalendarId: number | null;
  events: CalendarEvent[];
  currentDate: Date;
  viewMode: ViewMode;
  visibleCalendarIds: Set<number>;
  sidebarOpen: boolean;

  setCalendars: (calendars: Calendar[]) => void;
  selectCalendar: (id: number | null) => void;
  setEvents: (events: CalendarEvent[]) => void;
  setCurrentDate: (date: Date) => void;
  setViewMode: (mode: ViewMode) => void;
  navigateMonth: (direction: 1 | -1) => void;
  navigateWeek: (direction: 1 | -1) => void;
  toggleCalendarVisibility: (id: number) => void;
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
}

export const useCalendarStore = create<CalendarState>((set) => ({
  calendars: [],
  selectedCalendarId: null,
  events: [],
  currentDate: new Date(),
  viewMode: 'month',
  visibleCalendarIds: new Set<number>(),
  sidebarOpen: true,

  setCalendars: (calendars) =>
    set((state) => ({
      calendars,
      visibleCalendarIds: new Set([
        ...state.visibleCalendarIds,
        ...calendars.map((c) => c.calendarId),
      ]),
    })),

  selectCalendar: (id) => set({ selectedCalendarId: id }),

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

  toggleCalendarVisibility: (id) =>
    set((state) => {
      const next = new Set(state.visibleCalendarIds);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return { visibleCalendarIds: next };
    }),

  toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),

  setSidebarOpen: (open) => set({ sidebarOpen: open }),
}));
