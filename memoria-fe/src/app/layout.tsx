import type { Metadata } from 'next';
import Script from 'next/script';
import AuthProvider from '@/components/providers/AuthProvider';
import './globals.css';

const SITE_NAME = 'Memoria';
const SITE_DESC = '나만의 다이어리를 만들고, 꾸미고, 공유하세요. 커플, 가족, 팀과 함께하는 일정 관리 플랫폼';
const SITE_URL = 'https://memoria-6e7.pages.dev';

export const metadata: Metadata = {
  title: { default: `${SITE_NAME} - 나만의 다이어리`, template: `%s | ${SITE_NAME}` },
  description: SITE_DESC,
  keywords: [
    '다이어리', '일정관리', '캘린더', '커플다이어리', '가족캘린더',
    '공유일정', '웨딩플래너', 'diary', 'calendar', 'planner',
    'Memoria', '메모리아'
  ],
  icons: {
    icon: [{ url: '/favicon.ico', sizes: '32x32' }],
    apple: '/apple-touch-icon.png',
  },
  manifest: '/manifest.json',
  openGraph: {
    title: `${SITE_NAME} - 나만의 다이어리`,
    description: SITE_DESC,
    type: 'website',
    url: SITE_URL,
    siteName: SITE_NAME,
    images: [{ url: `${SITE_URL}/og-image.png`, width: 1200, height: 630 }],
    locale: 'ko_KR',
  },
  twitter: {
    card: 'summary_large_image',
    title: `${SITE_NAME} - 나만의 다이어리`,
    description: SITE_DESC,
    images: [`${SITE_URL}/og-image.png`],
  },
  robots: {
    index: true,
    follow: true,
  },
  // 도메인 확보 후 검색엔진 인증코드 추가
  // verification: {
  //   google: '{구글 인증코드}',
  //   other: { 'naver-site-verification': '{네이버 인증코드}' },
  // },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <head>
        {/* Schema.org 구조화 데이터 */}
        <script
          type="application/ld+json"
          dangerouslySetInnerHTML={{
            __html: JSON.stringify({
              '@context': 'https://schema.org',
              '@type': 'SoftwareApplication',
              name: SITE_NAME,
              url: SITE_URL,
              logo: `${SITE_URL}/logo.png`,
              description: SITE_DESC,
              applicationCategory: 'LifestyleApplication',
              operatingSystem: 'Web',
              offers: {
                '@type': 'Offer',
                price: '0',
                priceCurrency: 'KRW',
              },
            }),
          }}
        />
      </head>
      <body>
        <AuthProvider>
        {children}
        </AuthProvider>

        {/* Microsoft Clarity */}
        <Script id="clarity" strategy="afterInteractive">
          {`
            (function(c,l,a,r,i,t,y){
              c[a]=c[a]||function(){(c[a].q=c[a].q||[]).push(arguments)};
              t=l.createElement(r);t.async=1;t.src="https://www.clarity.ms/tag/"+i;
              y=l.getElementsByTagName(r)[0];y.parentNode.insertBefore(t,y);
            })(window,document,"clarity","script","${process.env.NEXT_PUBLIC_CLARITY_ID || 'PLACEHOLDER'}");
          `}
        </Script>

        {/* Google Analytics (GA4) */}
        {process.env.NEXT_PUBLIC_GA_ID && (
          <>
            <Script
              src={`https://www.googletagmanager.com/gtag/js?id=${process.env.NEXT_PUBLIC_GA_ID}`}
              strategy="afterInteractive"
            />
            <Script id="ga4" strategy="afterInteractive">
              {`
                window.dataLayer = window.dataLayer || [];
                function gtag(){dataLayer.push(arguments);}
                gtag('js', new Date());
                gtag('config', '${process.env.NEXT_PUBLIC_GA_ID}');
              `}
            </Script>
          </>
        )}
      </body>
    </html>
  );
}
