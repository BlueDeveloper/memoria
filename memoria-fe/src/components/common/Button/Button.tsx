'use client';

import { ButtonHTMLAttributes } from 'react';
import styles from './Button.module.css';

type ButtonVariant =
  | 'primary'
  | 'secondary'
  | 'outline'
  | 'social-kakao'
  | 'social-google'
  | 'social-apple';

type ButtonSize = 'sm' | 'md' | 'lg';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  fullWidth?: boolean;
  loading?: boolean;
}

const variantClassMap: Record<ButtonVariant, string> = {
  primary: styles.primary,
  secondary: styles.secondary,
  outline: styles.outline,
  'social-kakao': styles.socialKakao,
  'social-google': styles.socialGoogle,
  'social-apple': styles.socialApple,
};

export default function Button({
  variant = 'primary',
  size = 'md',
  fullWidth = false,
  loading = false,
  disabled,
  className,
  children,
  ...props
}: ButtonProps) {
  const classNames = [
    styles.button,
    variantClassMap[variant],
    styles[size],
    fullWidth && styles.fullWidth,
    loading && styles.loading,
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <button
      className={classNames}
      disabled={disabled || loading}
      {...props}
    >
      {loading && <span className={styles.spinner} />}
      {children}
    </button>
  );
}
