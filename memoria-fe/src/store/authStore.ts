import { create } from 'zustand';
import { User } from '@/types/auth';
import { getAccessToken, setAccessToken as setApiAccessToken } from '@/lib/api';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  setUser: (user: User) => void;
  clearUser: () => void;
  setAccessToken: (token: string) => void;
  getAccessToken: () => string | null;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,

  setUser: (user: User) => set({ user, isAuthenticated: true }),

  clearUser: () => {
    setApiAccessToken(null);
    set({ user: null, isAuthenticated: false });
  },

  setAccessToken: (token: string) => {
    setApiAccessToken(token);
  },

  getAccessToken: () => {
    return getAccessToken();
  },
}));
