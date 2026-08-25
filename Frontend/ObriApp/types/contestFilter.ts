// GET /api/contests가 category 필터만 지원(서버 정렬은 항상 마감임박순 고정)
export interface ContestFilter {
  categories: string[];
}

export const DEFAULT_CONTEST_FILTER: ContestFilter = {
  categories: [],
};
