'use client';

import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { format } from 'date-fns';
import { X, MapPin, AlarmClock, Repeat, Palette } from 'lucide-react';
import Button from '@/components/common/Button/Button';
import Input from '@/components/common/Input/Input';
import { useDiaryStore } from '@/store/diaryStore';
import { DiaryEvent } from '@/types/diary';
import { createEvent, updateEvent } from '@/lib/diaryApi';
import styles from './EventModal.module.css';

const REPEAT_OPTIONS = [
  { value: 'NONE', label: '반복 안함' },
  { value: 'DAILY', label: '매일' },
  { value: 'WEEKLY', label: '매주' },
  { value: 'MONTHLY', label: '매월' },
  { value: 'YEARLY', label: '매년' },
] as const;

const REMIND_OPTIONS = [
  { value: 0, label: '없음' },
  { value: 5, label: '5분 전' },
  { value: 15, label: '15분 전' },
  { value: 30, label: '30분 전' },
  { value: 60, label: '1시간 전' },
  { value: 1440, label: '1일 전' },
] as const;

const EVENT_COLORS = [
  '#E91E63', '#E17055', '#F39C12', '#C8A96E',
  '#27AE60', '#00B894', '#4A90D9', '#5B6ABF',
  '#7B68EE', '#9B59B6', '#1B2A4A', '#636E72',
];

const schema = z.object({
  title: z.string().min(1, '일정 제목을 입력해 주세요').max(100),
  description: z.string().max(500).optional(),
  location: z.string().max(200).optional(),
  startDate: z.string().min(1, '시작일을 선택해 주세요'),
  startTime: z.string(),
  endDate: z.string().min(1, '종료일을 선택해 주세요'),
  endTime: z.string(),
  allDay: z.boolean(),
  repeatType: z.enum(['NONE', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY']),
  remindMinutes: z.number(),
});

type FormData = z.infer<typeof schema>;

interface Props {
  onClose: () => void;
  event?: DiaryEvent | null;
  initialDate?: Date;
  initialHour?: number;
}

export default function EventModal({ onClose, event, initialDate, initialHour }: Props) {
  const selectedDiaryId = useDiaryStore((s) => s.selectedDiaryId);
  const [selectedColor, setSelectedColor] = useState(event?.color ?? '');
  const [showColorPicker, setShowColorPicker] = useState(false);
  const isEdit = !!event;

  const defaultDate = initialDate ?? new Date();
  const defaultHour = initialHour ?? new Date().getHours();

  const {
    register,
    handleSubmit,
    control,
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: event
      ? {
          title: event.title,
          description: event.description ?? '',
          location: event.location ?? '',
          startDate: event.startDt.slice(0, 10),
          startTime: format(new Date(event.startDt), 'HH:mm'),
          endDate: event.endDt.slice(0, 10),
          endTime: format(new Date(event.endDt), 'HH:mm'),
          allDay: event.allDay,
          repeatType: event.repeatType,
          remindMinutes: event.remindMinutes ?? 0,
        }
      : {
          title: '',
          description: '',
          location: '',
          startDate: format(defaultDate, 'yyyy-MM-dd'),
          startTime: `${String(defaultHour).padStart(2, '0')}:00`,
          endDate: format(defaultDate, 'yyyy-MM-dd'),
          endTime: `${String(defaultHour + 1).padStart(2, '0')}:00`,
          allDay: false,
          repeatType: 'NONE' as const,
          remindMinutes: 0,
        },
  });

  const allDay = watch('allDay');

  useEffect(() => {
    if (allDay) {
      setValue('startTime', '00:00');
      setValue('endTime', '23:59');
    }
  }, [allDay, setValue]);

  const onSubmit = async (data: FormData) => {
    const resolvedDiaryId = event?.diaryId ?? selectedDiaryId ?? 0;

    // 음수(샘플) diaryId로는 API 호출 불가
    if (resolvedDiaryId <= 0) {
      console.warn('샘플 다이어리에서는 일정을 저장할 수 없습니다.');
      return;
    }

    const startDt = data.allDay
      ? `${data.startDate}T00:00:00`
      : `${data.startDate}T${data.startTime}:00`;
    const endDt = data.allDay
      ? `${data.endDate}T23:59:59`
      : `${data.endDate}T${data.endTime}:00`;

    const payload = {
      diaryId: resolvedDiaryId,
      title: data.title,
      description: data.description || undefined,
      location: data.location || undefined,
      startDt,
      endDt,
      allDay: data.allDay,
      color: selectedColor || undefined,
      repeatType: data.repeatType,
      remindMinutes: data.remindMinutes || undefined,
    };

    console.log('Event payload:', JSON.stringify(payload));
    try {
      if (isEdit) {
        await updateEvent(event.eventId, payload);
      } else {
        await createEvent(payload);
      }
    } catch (err) {
      console.error('이벤트 저장 실패:', err);
      return; // 실패 시 모달 닫지 않음
    }
    onClose();
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <h2 className={styles.title}>{isEdit ? '일정 수정' : '새 일정'}</h2>
          <button className={styles.closeButton} onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className={styles.form}>
          {/* 제목 */}
          <Input
            label="제목"
            placeholder="일정 제목"
            error={errors.title?.message}
            registration={register('title')}
          />



          {/* 종일 토글 */}
          <div className={styles.row}>
            <label className={styles.checkboxLabel}>
              <input type="checkbox" {...register('allDay')} className={styles.checkbox} />
              종일
            </label>
          </div>

          {/* 날짜/시간 */}
          <div className={styles.dateTimeGroup}>
            <div className={styles.dateTimeRow}>
              <input type="date" className={styles.dateInput} {...register('startDate')} />
              {!allDay && (
                <input type="time" className={styles.timeInput} {...register('startTime')} />
              )}
            </div>
            <span className={styles.dateSep}>~</span>
            <div className={styles.dateTimeRow}>
              <input type="date" className={styles.dateInput} {...register('endDate')} />
              {!allDay && (
                <input type="time" className={styles.timeInput} {...register('endTime')} />
              )}
            </div>
          </div>
          {(errors.startDate || errors.endDate) && (
            <span className={styles.error}>{errors.startDate?.message || errors.endDate?.message}</span>
          )}

          {/* 장소 */}
          <div className={styles.iconField}>
            <MapPin size={16} className={styles.fieldIcon} />
            <input
              type="text"
              className={styles.iconInput}
              placeholder="장소 추가"
              {...register('location')}
            />
          </div>

          {/* 메모 */}
          <div className={styles.field}>
            <textarea
              className={styles.textarea}
              placeholder="메모 추가"
              rows={3}
              {...register('description')}
            />
          </div>

          {/* 반복 */}
          <div className={styles.iconField}>
            <Repeat size={16} className={styles.fieldIcon} />
            <select className={styles.iconSelect} {...register('repeatType')}>
              {REPEAT_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>

          {/* 알림 */}
          <div className={styles.iconField}>
            <AlarmClock size={16} className={styles.fieldIcon} />
            <Controller
              name="remindMinutes"
              control={control}
              render={({ field }) => (
                <select
                  className={styles.iconSelect}
                  value={field.value}
                  onChange={(e) => field.onChange(Number(e.target.value))}
                >
                  {REMIND_OPTIONS.map((opt) => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
              )}
            />
          </div>

          {/* 색상 */}
          <div className={styles.iconField}>
            <Palette size={16} className={styles.fieldIcon} />
            <button
              type="button"
              className={styles.colorButton}
              onClick={() => setShowColorPicker(!showColorPicker)}
            >
              <span
                className={styles.colorPreview}
                style={{ backgroundColor: selectedColor || 'var(--color-text-light)' }}
              />
              {selectedColor ? '색상 변경' : '색상 추가'}
            </button>
          </div>
          {showColorPicker && (
            <div className={styles.colorPicker}>
              <button
                type="button"
                className={`${styles.colorDot} ${!selectedColor ? styles.colorDotSelected : ''}`}
                style={{ backgroundColor: 'var(--color-text-light)' }}
                onClick={() => { setSelectedColor(''); setShowColorPicker(false); }}
              />
              {EVENT_COLORS.map((color) => (
                <button
                  key={color}
                  type="button"
                  className={`${styles.colorDot} ${selectedColor === color ? styles.colorDotSelected : ''}`}
                  style={{ backgroundColor: color }}
                  onClick={() => { setSelectedColor(color); setShowColorPicker(false); }}
                />
              ))}
            </div>
          )}

          {/* 액션 */}
          <div className={styles.actions}>
            <Button type="button" variant="outline" onClick={onClose}>취소</Button>
            <Button type="submit" variant="primary" loading={isSubmitting}>
              {isEdit ? '수정' : '저장'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
