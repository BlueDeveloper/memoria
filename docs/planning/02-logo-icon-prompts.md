# Memoria — 웹 로고 & 앱 아이콘 생성 프롬프트

> 작성일: 2026-04-30
> 생성 도구: Hicks Field (힉스필드)
> 포인트: 다이어리 모양, 단추버튼식 잠금, AI 느낌 배제, 모던 & 심플

***

## 1. 웹 로고 (메인 로고)

### 프롬프트 A — 정면 다이어리 + 단추 잠금

```
A minimalist flat logo design for a calendar sharing app called "Memoria".
The logo features a sleek closed diary/planner viewed from the front, with a small round snap button clasp in the center.
The diary has slightly rounded corners and a clean leather-like texture with subtle stitching along the spine edge.
Color palette: deep navy blue (#1B2A4A) diary body with a warm gold (#C8A96E) snap button accent.
No gradients, no 3D effects, no AI-generated artifacts.
Hand-crafted artisan quality, modern and sophisticated.
White or transparent background.
Simple geometric shapes, no unnecessary decoration.
Professional brand logo suitable for web header display.
```

### 프롬프트 B — 다이어리 아이콘 + 캘린더 힌트

```
A modern minimal logo mark for "Memoria", a shared calendar service.
Design shows a stylized diary/journal book icon with a circular snap button closure at the right edge.
Inside the diary cover, subtle calendar grid lines are barely visible, hinting at its calendar function.
Flat design, clean vector style.
Color scheme: soft charcoal (#2D3436) with coral accent (#E17055) on the snap button.
No text in the logo mark.
Organic hand-drawn feel, NOT AI-generated looking.
Balanced proportions suitable for both large web headers and small favicons.
White background, no shadows, no gradients.
```

### 프롬프트 C — 열린 다이어리 + 연결 심볼

```
Minimalist logo for a calendar sharing app "Memoria".
A slightly open diary/planner with a snap button clasp visible on the front cover.
Two thin curved lines emerge from the open pages, symbolizing connection and sharing between people.
Flat vector style, geometric and clean.
Primary color: slate blue (#5F7ADB), snap button: warm beige (#D4A574).
Modern Scandinavian design aesthetic.
No AI artifacts, no overly complex details, no photorealistic elements.
Feels handcrafted and intentional, like a carefully designed stationery brand logo.
Transparent background.
```

***

## 2. 앱 아이콘 (1024x1024 기본)

### 프롬프트 D — 앱 아이콘 (다이어리 + 단추)

```
App icon design, 1024x1024 pixels, for a calendar sharing app called "Memoria".
Centered diary/planner icon with a prominent round snap button clasp.
The diary shape fills most of the icon canvas with comfortable padding.
Rounded square icon shape (iOS/Android standard).
Background: deep navy (#1B2A4A).
Diary silhouette: white or light cream (#FFF8F0).
Snap button: warm gold (#C8A96E).
Ultra clean, minimal, no text, no gradients.
Reads clearly at 16x16 and 1024x1024.
Professional quality, handcrafted feel, absolutely no AI-generated look.
Inspired by premium stationery brand aesthetics.
```

### 프롬프트 E — 앱 아이콘 (미니멀 변형)

```
Minimal app icon for "Memoria" calendar app.
A simple diary outline with a single circular dot representing a snap button closure.
Geometric, reduced to essential shapes only.
Background: warm off-white (#FAF6F1).
Diary outline: charcoal (#2D3436).
Snap button dot: coral (#E17055).
Borderless, clean, timeless design.
Must be legible at very small sizes (favicon, notification icon).
Hand-drawn quality with geometric precision.
No AI-generated feeling, no unnecessary complexity.
```

***

## 3. 워드마크 (텍스트 로고)

### 프롬프트 F — Memoria 워드마크

```
Typography logo for the word "Memoria" in a modern serif font with slight humanist characteristics.
Clean letterforms with subtle contrast between thick and thin strokes.
The letter "M" has a slightly distinctive treatment — perhaps a small snap button dot integrated above or beside it.
Color: deep navy (#1B2A4A) on white background.
Elegant but approachable, not overly formal.
Feels like a premium stationery or lifestyle brand.
No AI-generated artifacts, hand-refined typography quality.
Kerning is precise, letter spacing is comfortable for both web headers and mobile displays.
```

***

## 4. 컬러 팔레트 제안

| 용도         | 색상        | HEX     | 설명             |
| ---------- | --------- | ------- | -------------- |
| Primary    | Deep Navy | #1B2A4A | 다이어리 본체, 신뢰감   |
| Accent     | Warm Gold | #C8A96E | 단추 버튼, 프리미엄 느낌 |
| Alt Accent | Coral     | #E17055 | 활동적 포인트 색상     |
| Background | Off White | #FAF6F1 | 종이 질감 배경       |
| Text       | Charcoal  | #2D3436 | 본문 텍스트         |
| Light      | Cream     | #FFF8F0 | 밝은 영역 배경       |

***

## 5. 해상도별 제작 가이드

로고 확정 후 아래 해상도로 리사이즈:

### 웹 로고

| 용도               | 크기              | 포맷        |
| ---------------- | --------------- | --------- |
| 웹 헤더             | 200x50 px       | SVG + PNG |
| Favicon          | 32x32, 16x16 px | ICO, PNG  |
| OG Image         | 1200x630 px     | PNG       |
| Apple Touch Icon | 180x180 px      | PNG       |

### 앱 아이콘

| 플랫폼              | 크기                      | 비고                         |
| ---------------- | ----------------------- | -------------------------- |
| iOS              | 1024x1024 → 자동 리사이즈     | Xcode Asset Catalog        |
| Android          | 512x512 (Play Store)    | Adaptive Icon 포함           |
| Android Adaptive | 108x108 dp (432x432 px) | Foreground + Background 분리 |
| Notification     | 96x96, 48x48, 24x24     | 모노크롬                       |

### 공통

| 용도         | 크기               | 포맷        |
| ---------- | ---------------- | --------- |
| 소셜 미디어 프로필 | 400x400 px       | PNG       |
| 이메일 시그니처   | 100x30 px        | PNG       |
| 로딩 스플래시    | 300x300 px (로고만) | SVG + PNG |

