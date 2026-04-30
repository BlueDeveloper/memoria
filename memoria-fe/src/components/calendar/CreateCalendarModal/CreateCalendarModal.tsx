'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { X } from 'lucide-react';
import Button from '@/components/common/Button/Button';
import Input from '@/components/common/Input/Input';
import { createCalendar } from '@/lib/calendarApi';
import styles from './CreateCalendarModal.module.css';

const PRESET_COLORS = [
  '#4A90D9', '#5B6ABF', '#7B68EE', '#9B59B6',
  '#E91E63', '#E17055', '#F39C12', '#C8A96E',
  '#27AE60', '#00B894', '#00CEC9', '#0984E3',
  '#1B2A4A', '#636E72', '#2D3436', '#B2BEC3',
];

const GROUP_TYPES = [
  { value: 'COUPLE', label: '커플', emoji: '💑' },
  { value: 'FAMILY', label: '가족', emoji: '👨‍👩‍👧‍👦' },
  { value: 'TEAM', label: '팀', emoji: '👥' },
  { value: 'OTHER', label: '기타', emoji: '📌' },
];

const schema = z.object({
  name: z.string().min(1, '캘린더 이름을 입력해 주세요').max(50, '50자 이내로 입력해 주세요'),
  description: z.string().max(200, '200자 이내로 입력해 주세요').optional(),
});

type FormData = z.infer<typeof schema>;

interface Props {
  onClose: () => void;
}

export default function CreateCalendarModal({ onClose }: Props) {
  const [selectedColor, setSelectedColor] = useState(PRESET_COLORS[0]);
  const [selectedGroupType, setSelectedGroupType] = useState('COUPLE');

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (data: FormData) => {
    try {
      await createCalendar({
        name: data.name,
        description: data.description,
        color: selectedColor,
        groupType: selectedGroupType,
      });
    } catch (err) {
      console.error('캘린더 생성 실패:', err);
    }
    onClose();
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <h2 className={styles.title}>새 캘린더</h2>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className={styles.form}>
          <Input
            label="캘린더 이름"
            placeholder="예: 우리 커플 일정"
            error={errors.name?.message}
            registration={register('name')}
          />

          {/* 그룹 유형 */}
          <div className={styles.field}>
            <label className={styles.label}>그룹 유형</label>
            <div className={styles.groupTypes}>
              {GROUP_TYPES.map((type) => (
                <button
                  key={type.value}
                  type="button"
                  className={`${styles.groupCard} ${selectedGroupType === type.value ? styles.groupCardSelected : ''}`}
                  onClick={() => setSelectedGroupType(type.value)}
                >
                  <span className={styles.groupEmoji}>{type.emoji}</span>
                  <span className={styles.groupLabel}>{type.label}</span>
                </button>
              ))}
            </div>
          </div>

          {/* 색상 선택 */}
          <div className={styles.field}>
            <label className={styles.label}>색상</label>
            <div className={styles.colorGrid}>
              {PRESET_COLORS.map((color) => (
                <button
                  key={color}
                  type="button"
                  className={`${styles.colorDot} ${selectedColor === color ? styles.colorDotSelected : ''}`}
                  style={{ backgroundColor: color }}
                  onClick={() => setSelectedColor(color)}
                />
              ))}
            </div>
          </div>

          {/* 설명 */}
          <div className={styles.field}>
            <label className={styles.label}>설명 (선택)</label>
            <textarea
              className={styles.textarea}
              placeholder="캘린더에 대한 간단한 설명"
              rows={3}
              {...register('description')}
            />
            {errors.description && (
              <span className={styles.error}>{errors.description.message}</span>
            )}
          </div>

          <div className={styles.actions}>
            <Button type="button" variant="outline" onClick={onClose}>
              취소
            </Button>
            <Button type="submit" variant="primary" loading={isSubmitting}>
              만들기
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
