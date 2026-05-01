import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Memoria - 나만의 다이어리',
  description: '나만의 다이어리를 만들고, 꾸미고, 공유하세요',
  icons: {
    icon: [
      { url: '/favicon.ico', sizes: '32x32' },
    ],
    apple: '/apple-touch-icon.png',
  },
  openGraph: {
    title: 'Memoria - 나만의 다이어리',
    description: '나만의 다이어리를 만들고, 꾸미고, 공유하세요',
    type: 'website',
    images: [{ url: '/og-image.png', width: 1200, height: 630 }],
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
