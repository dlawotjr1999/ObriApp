// 임시 더미 데이터. 콩쿠르 API(GET /api/contests) 연동 시 제거.
// 크롤링 기반 데이터라 목록 응답에 상세 정보(ContestDetail)가 모두 포함될
// 가능성이 있음 — 별도 상세조회(GET /api/contests/{id})는 백엔드 설계 확정 후 필요 여부 판단.
import { ContestDetail } from "@/types/contest";

export const MOCK_CONTESTS: ContestDetail[] = [
  {
    id: 1,
    title: "제35회 한국음악콩쿠르",
    category: "피아노",
    targetInstrument: "피아노",
    deadline: "2026-07-10",
    startDate: "2026-08-01",
    endDate: "2026-08-03",
    organizer: "한국음악협회",
    sourceUrl: "https://contest.co.kr/contest/view/1",
  },
  {
    id: 2,
    title: "서울국제바이올린콩쿠르",
    category: "현악",
    targetInstrument: "바이올린",
    deadline: "2026-07-20",
    startDate: "2026-09-15",
    endDate: "2026-09-20",
    organizer: "서울문화재단",
    sourceUrl: "https://contest.co.kr/contest/view/2",
  },
  {
    id: 3,
    title: "전국관악콩쿠르",
    category: "관악",
    targetInstrument: "플루트",
    deadline: "2026-07-07",
    startDate: "2026-08-15",
    endDate: "2026-08-16",
    organizer: "대한관악협회",
    sourceUrl: "https://contest.co.kr/contest/view/3",
  },
  {
    id: 4,
    title: "대한민국성악콩쿠르",
    category: "성악",
    targetInstrument: "소프라노",
    deadline: "2026-06-25",
    startDate: "2026-07-20",
    endDate: "2026-07-21",
    organizer: "대한성악협회",
    sourceUrl: "https://contest.co.kr/contest/view/4",
  },
  {
    id: 5,
    title: "청소년첼로콩쿠르",
    category: "현악",
    targetInstrument: "첼로",
    deadline: "2026-08-05",
    startDate: "2026-09-01",
    endDate: "2026-09-02",
    organizer: "한국첼로협회",
    sourceUrl: "https://contest.co.kr/contest/view/5",
  },
];
