'use client';

import { useEffect, useState } from 'react';
import { useAuthStore } from '@/store/authStore';
import api, { setAccessToken } from '@/lib/api';
import type { ApiResponse } from '@/types/api';
import type { TokenResponse, User } from '@/types/auth';

export default function AuthProvider({ children }: { children: React.ReactNode }) {
  const setUser = useAuthStore((s) => s.setUser);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      setInitialized(true);
      return;
    }

    const restoreAuth = async () => {
      try {
        // Refresh Token (httpOnly Cookie)으로 새 Access Token 발급
        const { data: tokenRes } = await api.post<ApiResponse<TokenResponse>>(
          '/api/auth/refresh'
        );
        setAccessToken(tokenRes.data.accessToken);

        // 사용자 정보 조회
        const { data: userRes } = await api.get<ApiResponse<User>>('/api/auth/me');
        setUser(userRes.data);
      } catch {
        // Refresh Token 없거나 만료 — 비로그인 상태 유지
      } finally {
        setInitialized(true);
      }
    };

    restoreAuth();
  }, [isAuthenticated, setUser]);

  if (!initialized) {
    return null;
  }

  return <>{children}</>;
}
