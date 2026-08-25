// 콩쿠르 조회 API (GET /api/contests, GET /api/contests/{id})
import { apiRequest } from "@/lib/apiClient";
import { ContestDetail } from "@/types/contest";
import { PageResponse } from "@/types/api";

export function getContests(categories: string[], page: number) {
  const params = new URLSearchParams({ page: String(page) });
  categories.forEach((c) => params.append("category", c));

  return apiRequest<PageResponse<ContestDetail>>(`/api/contests?${params.toString()}`);
}

export function getContest(id: number) {
  return apiRequest<ContestDetail>(`/api/contests/${id}`);
}
