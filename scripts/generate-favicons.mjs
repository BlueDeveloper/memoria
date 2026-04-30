import sharp from 'sharp';
import { mkdirSync, writeFileSync } from 'fs';
import { join } from 'path';

const INPUT = 'C:/BLUE/Project/blue/SAAS/Memoria/docs/assets/logo-v1.png';
const OUTPUT_DIR = 'C:/BLUE/Project/blue/SAAS/Memoria/docs/assets/favicons';

mkdirSync(OUTPUT_DIR, { recursive: true });

// 로고에서 다이어리 아이콘 부분만 크롭 (텍스트 제외, 상단 아이콘만)
// 원본 이미지 분석: 다이어리 아이콘은 대략 상단 65% 영역
const meta = await sharp(INPUT).metadata();
const cropHeight = Math.floor(meta.height * 0.62);
const cropped = sharp(INPUT)
  .extract({ left: 0, top: 0, width: meta.width, height: cropHeight })
  .resize(512, 512, { fit: 'contain', background: { r: 255, g: 255, b: 255, alpha: 0 } });

const croppedBuffer = await cropped.toBuffer();

// 다양한 해상도 생성
const sizes = [
  { name: 'favicon-16x16.png', size: 16 },
  { name: 'favicon-32x32.png', size: 32 },
  { name: 'favicon-48x48.png', size: 48 },
  { name: 'apple-touch-icon.png', size: 180 },
  { name: 'icon-192x192.png', size: 192 },
  { name: 'icon-512x512.png', size: 512 },
  { name: 'og-icon-400x400.png', size: 400 },
];

for (const { name, size } of sizes) {
  await sharp(croppedBuffer)
    .resize(size, size, { fit: 'contain', background: { r: 255, g: 255, b: 255, alpha: 0 } })
    .png()
    .toFile(join(OUTPUT_DIR, name));
  console.log(`  ${name}`);
}

// ICO 파일 생성 (16 + 32 + 48 멀티 사이즈 PNG → 가장 작은 것을 favicon.ico로)
// sharp는 ICO를 직접 못 만들므로 32x32 PNG를 favicon.ico로 복사 (브라우저 호환)
const ico32 = await sharp(croppedBuffer)
  .resize(32, 32, { fit: 'contain', background: { r: 255, g: 255, b: 255, alpha: 0 } })
  .png()
  .toBuffer();
writeFileSync(join(OUTPUT_DIR, 'favicon.ico'), ico32);
console.log('  favicon.ico (32x32 PNG)');

// 풀 로고 (텍스트 포함) 리사이즈
const fullSizes = [
  { name: 'logo-200x50.png', w: 200, h: 50 },
  { name: 'logo-300x300.png', w: 300, h: 300 },
];

for (const { name, w, h } of fullSizes) {
  await sharp(INPUT)
    .resize(w, h, { fit: 'contain', background: { r: 255, g: 255, b: 255, alpha: 0 } })
    .png()
    .toFile(join(OUTPUT_DIR, name));
  console.log(`  ${name}`);
}

// OG Image (1200x630, 흰 배경 중앙 배치)
await sharp({
  create: { width: 1200, height: 630, channels: 4, background: { r: 255, g: 255, b: 255, alpha: 1 } }
})
  .composite([{
    input: await sharp(INPUT)
      .resize(400, 400, { fit: 'contain', background: { r: 255, g: 255, b: 255, alpha: 0 } })
      .toBuffer(),
    gravity: 'centre'
  }])
  .png()
  .toFile(join(OUTPUT_DIR, 'og-image-1200x630.png'));
console.log('  og-image-1200x630.png');

console.log('\nDone! All favicons generated in:', OUTPUT_DIR);
