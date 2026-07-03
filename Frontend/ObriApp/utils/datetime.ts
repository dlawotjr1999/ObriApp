// 날짜/시간 포맷 유틸. 순수 함수만 모음 (외부 의존 없음).

/**
 * ISO 8601 문자열을 받아 오늘 기준 D-day 문자열 반환.
 * D-day → "D-day" / D-3 → "D-3" / 지난 날짜 → "D+1"
 */
export function getDday(iso: string): { label: string; urgent: boolean; expired: boolean } {
  const event = new Date(iso);
  if (Number.isNaN(event.getTime())) return { label: "", urgent: false, expired: false };

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  event.setHours(0, 0, 0, 0);

  const diff = Math.round((event.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

  if (diff === 0) return { label: "D-day", urgent: true, expired: false };
  if (diff > 0) return { label: `D-${diff}`, urgent: diff <= 3, expired: false };
  return { label: `D+${Math.abs(diff)}`, urgent: false, expired: true };
}

const WEEKDAYS = ["일", "월", "화", "수", "목", "금", "토"];

const pad = (n: number) => String(n).padStart(2, "0");

/**
 * ISO 8601 문자열을 "YYYY.MM.DD (요일) HH:mm" 형태로 포맷.
 * 예: "2024-05-01T14:00:00" -> "2024.05.01 (수) 14:00"
 * 파싱 불가한 값이면 원본 문자열을 그대로 반환.
 */
export function formatEventDateTime(iso: string): string {
  const date = new Date(iso);
  if (Number.isNaN(date.getTime())) return iso;

  const y = date.getFullYear();
  const m = pad(date.getMonth() + 1);
  const d = pad(date.getDate());
  const weekday = WEEKDAYS[date.getDay()];
  const hh = pad(date.getHours());
  const mm = pad(date.getMinutes());

  return `${y}.${m}.${d} (${weekday}) ${hh}:${mm}`;
}
