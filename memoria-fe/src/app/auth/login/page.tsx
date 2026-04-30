'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import Button from '@/components/common/Button/Button';
import Input from '@/components/common/Input/Input';
import { useAuthStore } from '@/store/authStore';
import api from '@/lib/api';
import type { ApiResponse } from '@/types/api';
import type { TokenResponse, User } from '@/types/auth';
import styles from './page.module.css';

const loginSchema = z.object({
  email: z.string().min(1, '이메일을 입력해 주세요').email('올바른 이메일 형식을 입력해 주세요'),
  password: z.string().min(1, '비밀번호를 입력해 주세요'),
});

type LoginForm = z.infer<typeof loginSchema>;

export default function LoginPage() {
  const router = useRouter();
  const { setUser, setAccessToken } = useAuthStore();
  const [serverError, setServerError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (formData: LoginForm) => {
    setServerError('');
    setIsLoading(true);
    try {
      const { data: tokenRes } = await api.post<ApiResponse<TokenResponse>>(
        '/api/auth/login',
        formData
      );
      setAccessToken(tokenRes.data.accessToken);

      const { data: userRes } = await api.get<ApiResponse<User>>('/api/auth/me');
      setUser(userRes.data);

      router.push('/calendar');
    } catch {
      setServerError('이메일 또는 비밀번호가 올바르지 않습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSocialLogin = (provider: 'kakao' | 'google' | 'apple') => {
    const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    window.location.href = `${baseUrl}/oauth2/authorization/${provider}`;
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.logoSection}>
          <h1 className={styles.logo}>Memoria</h1>
          <p className={styles.subtitle}>함께 만드는 우리의 시간</p>
        </div>

        <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
          {serverError && (
            <div className={styles.serverError}>{serverError}</div>
          )}

          <Input
            label="이메일"
            type="email"
            placeholder="이메일 주소"
            error={errors.email?.message}
            registration={register('email')}
          />

          <Input
            label="비밀번호"
            type="password"
            placeholder="비밀번호"
            error={errors.password?.message}
            registration={register('password')}
          />

          <Button
            type="submit"
            variant="primary"
            size="lg"
            fullWidth
            loading={isLoading}
          >
            로그인
          </Button>
        </form>

        <div className={styles.divider}>
          <span className={styles.dividerLine} />
          <span className={styles.dividerText}>또는</span>
          <span className={styles.dividerLine} />
        </div>

        <div className={styles.socialButtons}>
          <Button
            variant="social-kakao"
            size="lg"
            fullWidth
            onClick={() => handleSocialLogin('kakao')}
          >
            <span className={styles.socialIcon}>&#x1F7E1;</span>
            카카오로 시작하기
          </Button>

          <Button
            variant="social-google"
            size="lg"
            fullWidth
            onClick={() => handleSocialLogin('google')}
          >
            <span className={styles.socialIcon}>G</span>
            Google로 시작하기
          </Button>

          <Button
            variant="social-apple"
            size="lg"
            fullWidth
            onClick={() => handleSocialLogin('apple')}
          >
            <span className={styles.socialIcon}>&#xF8FF;</span>
            Apple로 시작하기
          </Button>
        </div>

        <div className={styles.footer}>
          계정이 없으신가요?
          <Link href="/auth/register" className={styles.footerLink}>
            회원가입
          </Link>
        </div>
      </div>
    </div>
  );
}
