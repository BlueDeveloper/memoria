'use client';

import { useEffect, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useAuthStore } from '@/store/authStore';
import api, { setAccessToken } from '@/lib/api';
import type { ApiResponse } from '@/types/api';
import type { TokenResponse } from '@/types/auth';

function KakaoCallbackHandler() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const setUser = useAuthStore((s) => s.setUser);

  useEffect(() => {
    const code = searchParams.get('code');
    if (!code) {
      router.replace('/auth/login');
      return;
    }

    const handleCallback = async () => {
      try {
        const redirectUri = `${window.location.origin}/auth/callback/kakao`;
        const { data } = await api.post<ApiResponse<TokenResponse>>(
          '/api/auth/oauth/kakao',
          { code, redirectUri }
        );

        setAccessToken(data.data.accessToken);

        // 사용자 정보 조회
        const { data: userRes } = await api.get<ApiResponse<{ id: number; email: string; nickname: string; profileImage: string | null }>>(
          '/api/auth/me'
        );
        setUser(userRes.data);

        router.replace('/');
      } catch (err) {
        console.error('카카오 로그인 실패:', err);
        alert('로그인에 실패했습니다. 다시 시도해주세요.');
        router.replace('/');
      }
    };

    handleCallback();
  }, [searchParams, router, setUser]);

  return (
    <div style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      height: '100vh',
      fontSize: '16px',
      color: '#636E72',
    }}>
      로그인 중...
    </div>
  );
}

export default function KakaoCallbackPage() {
  return (
    <Suspense fallback={<div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh' }}>로그인 중...</div>}>
      <KakaoCallbackHandler />
    </Suspense>
  );
}
