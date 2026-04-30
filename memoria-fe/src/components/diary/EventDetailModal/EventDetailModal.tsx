'use client';

import { useState } from 'react';
import { format, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';
import {
  X,
  MapPin,
  Clock,
  Repeat,
  AlarmClock,
  Edit3,
  Trash2,
  Send,
} from 'lucide-react';
import { useDiaryStore } from '@/store/diaryStore';
import { DiaryEvent } from '@/types/diary';
import styles from './EventDetailModal.module.css';

interface Comment {
  commentId: number;
  memberNickname: string;
  content: string;
  createdAt: string;
}

const REPEAT_LABELS: Record<string, string> = {
  NONE: '',
  DAILY: '매일 반복',
  WEEKLY: '매주 반복',
  MONTHLY: '매월 반복',
  YEARLY: '매년 반복',
};

interface Props {
  event: DiaryEvent;
  onClose: () => void;
  onEdit: () => void;
}

export default function EventDetailModal({ event, onClose, onEdit }: Props) {
  const diaries = useDiaryStore((s) => s.diaries);
  const diary = diaries.find((d) => d.diaryId === event.diaryId);

  const [comments, setComments] = useState<Comment[]>([
    // 목업 댓글
    { commentId: 1, memberNickname: '나', content: '확인했어요!', createdAt: new Date().toISOString() },
  ]);
  const [newComment, setNewComment] = useState('');

  const eventColor = event.color ?? diary?.color ?? 'var(--color-primary)';

  const formatDateTime = () => {
    const start = parseISO(event.startDt);
    const end = parseISO(event.endDt);
    if (event.allDay) {
      return format(start, 'yyyy년 M월 d일 (EEE)', { locale: ko }) + ' 종일';
    }
    const startStr = format(start, 'yyyy년 M월 d일 (EEE) HH:mm', { locale: ko });
    const endStr = format(end, 'HH:mm', { locale: ko });
    return `${startStr} ~ ${endStr}`;
  };

  const handleAddComment = () => {
    if (!newComment.trim()) return;
    // TODO: API 연동
    setComments((prev) => [
      ...prev,
      {
        commentId: Date.now(),
        memberNickname: '나',
        content: newComment.trim(),
        createdAt: new Date().toISOString(),
      },
    ]);
    setNewComment('');
  };

  const handleDelete = () => {
    // TODO: API 연동 + 확인 다이얼로그
    console.log('Delete event:', event.eventId);
    onClose();
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        {/* 색상 바 */}
        <div className={styles.colorBar} style={{ backgroundColor: eventColor }} />

        <div className={styles.header}>
          <h2 className={styles.title}>{event.title}</h2>
          <div className={styles.headerActions}>
            <button className={styles.iconBtn} onClick={onEdit} title="수정">
              <Edit3 size={16} />
            </button>
            <button className={styles.iconBtn} onClick={handleDelete} title="삭제">
              <Trash2 size={16} />
            </button>
            <button className={styles.iconBtn} onClick={onClose}>
              <X size={18} />
            </button>
          </div>
        </div>

        <div className={styles.body}>
          {/* 날짜/시간 */}
          <div className={styles.infoRow}>
            <Clock size={16} className={styles.infoIcon} />
            <span>{formatDateTime()}</span>
          </div>

          {/* 반복 */}
          {event.repeatType !== 'NONE' && (
            <div className={styles.infoRow}>
              <Repeat size={16} className={styles.infoIcon} />
              <span>{REPEAT_LABELS[event.repeatType]}</span>
            </div>
          )}

          {/* 장소 */}
          {event.location && (
            <div className={styles.infoRow}>
              <MapPin size={16} className={styles.infoIcon} />
              <span>{event.location}</span>
            </div>
          )}

          {/* 알림 */}
          {event.remindMinutes && event.remindMinutes > 0 && (
            <div className={styles.infoRow}>
              <AlarmClock size={16} className={styles.infoIcon} />
              <span>
                {event.remindMinutes >= 1440
                  ? `${Math.floor(event.remindMinutes / 1440)}일 전`
                  : event.remindMinutes >= 60
                    ? `${Math.floor(event.remindMinutes / 60)}시간 전`
                    : `${event.remindMinutes}분 전`}
              </span>
            </div>
          )}

          {/* 메모 */}
          {event.description && (
            <div className={styles.description}>{event.description}</div>
          )}

          {/* 다이어리 + 생성자 */}
          <div className={styles.meta}>
            <span className={styles.calendarBadge} style={{ backgroundColor: diary?.color }}>
              {diary?.name}
            </span>
            <span className={styles.creator}>작성: {event.creatorNickname}</span>
          </div>
        </div>

        {/* 댓글 */}
        <div className={styles.commentsSection}>
          <h3 className={styles.commentsTitle}>댓글 ({comments.length})</h3>

          <div className={styles.commentList}>
            {comments.map((comment) => (
              <div key={comment.commentId} className={styles.commentItem}>
                <div className={styles.commentAvatar}>
                  {comment.memberNickname.charAt(0)}
                </div>
                <div className={styles.commentBody}>
                  <div className={styles.commentHeader}>
                    <span className={styles.commentAuthor}>{comment.memberNickname}</span>
                    <span className={styles.commentDate}>
                      {format(parseISO(comment.createdAt), 'M/d HH:mm')}
                    </span>
                  </div>
                  <p className={styles.commentText}>{comment.content}</p>
                </div>
              </div>
            ))}
          </div>

          <div className={styles.commentInput}>
            <input
              type="text"
              className={styles.commentField}
              placeholder="댓글 작성..."
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleAddComment()}
            />
            <button
              className={styles.sendButton}
              onClick={handleAddComment}
              disabled={!newComment.trim()}
            >
              <Send size={16} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
