// 숫자 포맷 유틸. 순수 함수만 모음.

// 천 단위 콤마 + "원". (Hermes의 Intl 미지원 대비해 정규식으로 직접 처리)
export function formatKRW(amount: number): string {
  const withComma = amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  return `${withComma}원`;
}
