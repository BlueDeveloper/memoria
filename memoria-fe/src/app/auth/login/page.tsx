'use client';

import Button from '@/components/common/Button/Button';
import { KakaoIcon, GoogleIcon, AppleIcon } from '@/components/icons/SocialIcons';
import styles from './page.module.css';

export default function LoginPage() {
  const handleSocialLogin = (provider: 'kakao' | 'google' | 'apple') => {
    const feOrigin = window.location.origin;
    const redirectUri = `${feOrigin}/auth/callback/${provider}`;

    if (provider === 'kakao') {
      const kakaoClientId = '916c4662c0d22467bbb876cf8a77521a';
      window.location.href = `https://kauth.kakao.com/oauth/authorize?client_id=${kakaoClientId}&redirect_uri=${encodeURIComponent(redirectUri)}&response_type=code`;
    }
    // TODO: google, apple
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.logoSection}>
          <img src="/logo.png" alt="Memoria" className={styles.logoImage} />
          <p className={styles.subtitle}>나만의 다이어리를 만들고, 꾸미고, 공유하세요</p>
        </div>

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

        <div className={styles.footer}>
          간편하게 소셜 계정으로 시작하세요
        </div>
      </div>
    </div>
  );
}
