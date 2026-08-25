// 콩쿠르(Contest) 도메인 타입. 노션 API 스펙(GET /api/contests) 기준.
// 화면·컴포넌트·더미데이터가 공유하는 단일 소스.

// 목록(전체 조회)에서 카드로 보여줄 때 필요한 최소 필드.
// 단건 조회의 endDate / sourceUrl 등은 ContestDetail에서 확장.
export interface ContestSummary {
  id: number;
  title: string;
  category: string;
  deadline: string; // 접수 마감일, "YYYY-MM-DD"
  startDate: string; // 대회 시작일, "YYYY-MM-DD"
  organizer: string;
}

// 단건 조회(GET /api/contests/{id}) 응답. 상세 모달에서 사용.
export interface ContestDetail extends ContestSummary {
  endDate: string; // 대회 종료일, "YYYY-MM-DD"
  sourceUrl: string; // 원문(크롤링 출처) 링크 — 지원하기 버튼 리다이렉트 대상
}
