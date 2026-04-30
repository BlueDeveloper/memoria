import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Memoria',
  description: '함께 만드는 우리의 시간 - 일정 공유 서비스',
  openGraph: {
    title: 'Memoria',
    description: '함께 만드는 우리의 시간 - 일정 공유 서비스',
    type: 'website',
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
