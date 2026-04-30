'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import Button from '@/components/common/Button/Button';
import Input from '@/components/common/Input/Input';
import api from '@/lib/api';
import styles from './page.module.css';

const registerSchema = z
  .object({
    email: z
      .string()
      .min(1, '이메일을 입력해 주세요')
      .email('올바른 이메일 형식을 입력해 주세요'),
    password: z
      .string()
      .min(8, '비밀번호는 8자 이상이어야 합니다')
      .regex(
        /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/,
        '영문, 숫자, 특수문자를 모두 포함해 주세요'
      ),
    passwordConfirm: z.string().min(1, '비밀번호 확인을 입력해 주세요'),
    nickname: z
      .string()
      .min(2, '닉네임은 2자 이상이어야 합니다')
      .max(20, '닉네임은 20자 이하여야 합니다'),
  })
  .refine((data) => data.password === data.passwordConfirm, {
    message: '비밀번호가 일치하지 않습니다',
    path: ['passwordConfirm'],
  });

type RegisterForm = z.infer<typeof registerSchema>;

export default function RegisterPage() {
  const router = useRouter();
  const [serverError, setServerError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (formData: RegisterForm) => {
    setServerError('');
    setIsLoading(true);
    try {
      await api.post('/api/auth/register', {
        email: formData.email,
        password: formData.password,
        nickname: formData.nickname,
      });
      router.push('/auth/login?registered=true');
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      setServerError(
        axiosError.response?.data?.message || '회원가입에 실패했습니다. 다시 시도해 주세요.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.logoSection}>
          <h1 className={styles.logo}>Memoria</h1>
          <p className={styles.title}>회원가입</p>
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
            placeholder="영문, 숫자, 특수문자 포함 8자 이상"
            error={errors.password?.message}
            registration={register('password')}
          />

          <Input
            label="비밀번호 확인"
            type="password"
            placeholder="비밀번호를 다시 입력해 주세요"
            error={errors.passwordConfirm?.message}
            registration={register('passwordConfirm')}
          />

          <Input
            label="닉네임"
            type="text"
            placeholder="2~20자"
            error={errors.nickname?.message}
            registration={register('nickname')}
          />

          <Button
            type="submit"
            variant="secondary"
            size="lg"
            fullWidth
            loading={isLoading}
            style={{ marginTop: 8 }}
          >
            가입하기
          </Button>
        </form>

        <div className={styles.footer}>
          이미 계정이 있으신가요?
          <Link href="/auth/login" className={styles.footerLink}>
            로그인
          </Link>
        </div>
      </div>
    </div>
  );
}
