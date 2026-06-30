// 날짜/시간 포맷 유틸. 순수 함수만 모음 (외부 의존 없음).

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
