# Memoria — 홍보 전략 & SEO/분석 설정

> 작성일: 2026-05-02

---

## 1. SEO 설정 현황

### 완료
- [x] 메타태그 (title, description, keywords, OG, Twitter Card)
- [x] Schema.org 구조화 데이터 (SoftwareApplication)
- [x] robots.txt (크롤러 허용, 콜백/API 차단)
- [x] sitemap.xml (메인, 다이어리, 로그인)
- [x] PWA manifest.json (아이콘, 테마색)
- [x] 파비콘/OG 이미지 전체 사이즈

### 도메인 확보 후 필요
- [ ] Google Search Console 등록 + sitemap 제출
- [ ] 네이버 서치어드바이저 등록 + sitemap 제출
- [ ] 검색엔진 소유권 인증 (HTML 파일 또는 meta)
- [ ] HTTPS 적용 (Cloudflare SSL)
- [ ] sitemap.xml URL 도메인 교체
- [ ] robots.txt Sitemap URL 교체

---

## 2. 분석 도구

### Microsoft Clarity (사용자 행동 분석)
- **용도**: 히트맵, 세션 녹화, 데드 클릭, 스크롤 깊이
- **비용**: 무료 (트래픽 무제한)
- **설정**: `.env.production`에 `NEXT_PUBLIC_CLARITY_ID` 입력
- **TODO**: https://clarity.microsoft.com 에서 프로젝트 생성 → ID 발급

### Google Analytics 4 (트래픽 분석)
- **용도**: 사용자 수, 페이지뷰, 유입 경로, 전환율
- **비용**: 무료
- **설정**: `.env.production`에 `NEXT_PUBLIC_GA_ID` 입력 (G-XXXXXXX)
- **TODO**: GA4 속성 생성 → 측정 ID 발급

### Cloudflare Analytics
- **용도**: 서버 관점 트래픽 (봇 포함)
- **비용**: 무료 (Cloudflare Pages 사용 시 자동)
- **설정**: 별도 불필요 — Cloudflare 대시보드에서 확인

---

## 3. 홍보 전략

### Phase 1 — 사전 홍보 (출시 전)

| 채널 | 전략 | 비용 |
|------|------|------|
| **인스타그램** | 다이어리/플래너 감성 피드 + 티저 영상 | 무료 |
| **틱톡** | "나만의 다이어리 만들기" 숏폼 | 무료 |
| **커뮤니티** | 에펨코리아, 클리앙, 디시 등 리뷰 게시 | 무료 |
| **블로그** | 네이버 블로그 SEO 포스팅 (키워드: 커플다이어리, 공유캘린더) | 무료 |
| **랜딩 페이지** | 사전 등록 (이메일 수집) → 출시 시 알림 | 무료 |

### Phase 2 — 출시 홍보

| 채널 | 전략 | 비용 |
|------|------|------|
| **Product Hunt** | 글로벌 런칭 — "Diary platform for couples & families" | 무료 |
| **앱스토어 ASO** | 키워드 최적화 (커플다이어리, 가족캘린더, 웨딩플래너) | 무료 |
| **카카오 비즈채널** | 고객 상담 + 알림톡 | 무료 |
| **인플루언서** | 커플/육아/웨딩 인플루언서 체험 리뷰 | 무료~유료 |
| **구글 Ads** | 검색 광고 ("커플 다이어리 앱", "공유 캘린더") | 유료 |

### Phase 3 — 성장 전략

| 전략 | 설명 |
|------|------|
| **초대 보상** | 친구 초대 시 티켓 5개 지급 (바이럴) |
| **시즌 이벤트** | 발렌타인/크리스마스/웨딩시즌 한정 테마 무료 배포 |
| **파트너십** | 웨딩 업체와 제휴 (웨딩 플래너 다이어리 프로모션) |
| **콘텐츠 마케팅** | "커플 일정 관리 꿀팁" 블로그 시리즈 |
| **UGC** | 사용자 다이어리 꾸미기 콘테스트 → SNS 공유 |

---

## 4. 타겟 키워드

### 메인 키워드 (검색량 높음)
- 커플 다이어리
- 공유 캘린더
- 가족 일정 관리
- 웨딩 플래너
- 다이어리 앱

### 롱테일 키워드 (전환율 높음)
- 커플 일정 공유 앱
- 가족 캘린더 공유
- 웨딩 준비 체크리스트 앱
- 다이어리 꾸미기 앱
- 무료 공유 다이어리

### 글로벌 키워드
- shared diary app
- couple calendar
- family planner
- wedding planner app
- diary customization

---

## 5. 설정 체크리스트

### 즉시 가능 (도메인 없이)
- [x] 메타태그 + OG 태그
- [x] Schema.org JSON-LD
- [x] robots.txt + sitemap.xml
- [x] PWA manifest
- [x] Clarity 스크립트 삽입 (ID만 넣으면 동작)
- [x] GA4 스크립트 삽입 (ID만 넣으면 동작)

### 도메인 확보 후
- [ ] Clarity 프로젝트 생성 → ID 발급 → .env 반영
- [ ] GA4 속성 생성 → 측정 ID → .env 반영
- [ ] Google Search Console 등록 + 인증
- [ ] 네이버 서치어드바이저 등록 + 인증
- [ ] 카카오 비즈채널 개설
- [ ] 인스타그램 계정 개설 (@memoria.diary)
