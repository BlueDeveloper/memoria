'use client';

import { useRouter } from 'next/navigation';
import { X, CalendarPlus } from 'lucide-react';
import Button from '@/components/common/Button/Button';
import { KakaoIcon, GoogleIcon, AppleIcon } from '@/components/icons/SocialIcons';
import styles from './AuthPromptModal.module.css';

interface Props {
  onClose: () => void;
}

export default function AuthPromptModal({ onClose }: Props) {
  const router = useRouter();

  const handleSocialLogin = (provider: 'kakao' | 'google' | 'apple') => {
    const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    window.location.href = `${baseUrl}/oauth2/authorization/${provider}`;
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <button className={styles.closeButton} onClick={onClose}>
          <X size={20} />
        </button>

        <div className={styles.iconCircle}>
          <CalendarPlus size={32} strokeWidth={1.5} />
        </div>

        <h2 className={styles.title}>일정을 함께 관리해보세요</h2>
        <p className={styles.description}>
          로그인하면 캘린더를 만들고, 일정을 공유하고,<br />
          소중한 사람들과 함께 시간을 관리할 수 있어요.
        </p>

        <div className={styles.socialButtons}>
          <Button
            variant="social-kakao"
            size="lg"
            fullWidth
            onClick={() => handleSocialLogin('kakao')}
          >
            <KakaoIcon size={18} />
            카카오로 시작하기
          </Button>

          <Button
            variant="social-google"
            size="lg"
            fullWidth
            onClick={() => handleSocialLogin('google')}
          >
            <GoogleIcon size={18} />
            Google로 시작하기
          </Button>

          <Button
            variant="social-apple"
            size="lg"
            fullWidth
            onClick={() => handleSocialLogin('apple')}
          >
            <AppleIcon size={18} />
            Apple로 시작하기
          </Button>
        </div>

        <div className={styles.divider}>
          <span className={styles.dividerLine} />
          <span className={styles.dividerText}>또는</span>
          <span className={styles.dividerLine} />
        </div>

        <Button
          variant="primary"
          size="lg"
          fullWidth
          onClick={() => router.push('/auth/register')}
        >
          이메일로 회원가입
        </Button>

        <div className={styles.footer}>
          이미 계정이 있으신가요?{' '}
          <button className={styles.loginLink} onClick={() => router.push('/auth/login')}>
            로그인
          </button>
        </div>
      </div>
    </div>
  );
}
