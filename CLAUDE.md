# CLAUDE.md — Memoria 프로젝트 가이드

## 글로벌 인프라 참조 🌐

📖 **[글로벌 인프라 참조 가이드](C:\Users\bluee.claude\INFRASTRUCTURE_GLOBAL_REFERENCE.md)**
📖 **[전역 개발 전략](C:\Users\bluee.claude\CLAUDE.md)**

***

## 프로젝트 개요

* **이름**: Memoria (메모리아)

* **컨셉**: 다이어리 플랫폼 — 나만의 다이어리를 만들고, 꾸미고, 공유하는 서비스

* **타겟**: 커플, 가족, 소규모 팀, 웨딩 준비 커플

* **수익 모델**: 티켓 시스템 (인앱 화폐) — 추가 다이어리, 프리미엄 로고/테마, 웨딩 플래너 등

* **핵심 흐름**: 인트로(다이어리 선택) → 다이어리 진입 → 캘린더/메모 → 상점(로고/테마 구매)

* **브랜드**: BRP (Blue Red Polarity)

## 기술 스택

### Phase 1 — 웹

| 항목        | 기술                                                                            |
| --------- | ----------------------------------------------------------------------------- |
| **FE**    | Next.js (App Router) + TypeScript + CSS Modules                               |
| **BE**    | Spring Boot 3.x + Java 17 + DDD                                               |
| **DB**    | Oracle (OCI Autonomous DB)                                                    |
| **Cache** | Redis                                                                         |
| **인증**    | JWT (Access 1h + Refresh 30d, Silent Refresh) + OAuth2 (Kakao, Google, Apple) |
| **FE 배포** | Cloudflare Pages                                                              |
| **BE 배포** | OCI Compute + Jenkins + systemctl                                             |

### Phase 2 — 모바일 앱 (추후)

| 항목            | 기술                           |
| ------------- | ---------------------------- |
| **FE**        | React Native (Expo)          |
| **Android**   | GitHub Actions → Google Play |
| **iOS**       | Codemagic → TestFlight       |
| **Bundle ID** | brp.memoria.app              |

## 배포 정보

| 항목         | 값                                   |
| ---------- | ----------------------------------- |
| **레포**     | BlueDeveloper/memoria               |
| **FE 배포**  | Cloudflare Pages (main push 자동)     |
| **BE 배포**  | OCI (152.69.235.170:8084) systemctl |
| **BE 서비스** | memoria-be.service (systemd)        |
| **DB 스키마** | MEMORIA (dev/prod 공유)               |
| **도메인**    | 미정                                  |

### 환경 분리

| 항목     | 개발 (dev)                 | 운영 (prod)                |
| ------ | ------------------------ | ------------------------ |
| FE URL | localhost:3000           | memoria.pages.dev        |
| BE URL | localhost:8080           | 152.69.235.170:8084      |
| DB     | MEMORIA\@BlueAutoDB (공유) | MEMORIA\@BlueAutoDB (공유) |
| Redis  | localhost:6379           | localhost:6379 (서버 내)    |
| BE 배포  | 로컬 gradlew bootRun       | scripts/deploy-be.sh     |

## 벤치마킹 대상

* **타임트리 (TimeTree)**: <https://timetreeapp.com>

  * 캘린더 공유, 이벤트 생성, 메모, 알림

  * 초대 링크로 그룹 참여

  * 일/주/월 뷰 전환

## 프로젝트 구조

```
Memoria/
├── memoria-fe/        # Next.js 15 프론트엔드
├── memoria-be/        # Spring Boot 3.4 백엔드
├── docs/
│   ├── sql/           # DDL (create.sql, delete.sql)
│   ├── planning/      # 기획 문서
│   ├── assets/        # 로고, 파비콘
│   └── work-log/      # 작업 로그
├── scripts/           # 유틸리티 스크립트
└── CLAUDE.md
```

## 작업 규칙

* 작업이 완료되면 반드시 변경사항을 커밋하고 원격 저장소에 푸시한다.

* 커밋 메시지는 한글로 작성한다.

* 작업 내역은 docs/work-log/ 에 기록한다.

* 로컬 빌드 성공 확인 후 배포한다.

## 다음 작업

1. ~~기획 — 핵심 기능 정의~~ ✅ (docs/planning/01-feature-definition.md)
2. ~~타임트리 벤치마킹~~ ✅ (docs/work-log/2026-04-30-timetree-benchmarking.md)
3. ~~기술 스택 확정~~ ✅ (docs/planning/01-feature-definition.md)
4. ~~프로젝트 초기 세팅~~ ✅ (memoria-fe + memoria-be)
5. ~~화면 설계~~ ✅ (docs/planning/03-wireframe.md)
6. ~~도메인 엔티티 + API~~ ✅ (인증/캘린더/이벤트/댓글)
7. ~~BE 배포~~ ✅ (OCI 152.69.235.170:8084)
8. ~~환경 분리~~ ✅ (dev/prod)
9. ~~OCI VCN 보안 목록 8084 포트~~ ✅
10. ~~FE Cloudflare Pages 배포~~ ✅
11. ~~카카오 로그인~~ ✅
12. 카카오 클라이언트 시크릿 재발급 후 BE 반영 (보안 강화)
13. 구글 로그인 구현
14. 애플 로그인 구현
15. Jenkins CI/CD 파이프라인 구성
16. 운영 서버 재배포 (Diary 리팩토링 + 카카오 로그인 반영)
17. 도메인 확보 후 HTTPS 설정

