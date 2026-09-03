// 콩쿠르 조회 API (GET /api/contests, GET /api/contests/{id})
import { apiRequest } from "@/lib/apiClient";
import { ContestDetail } from "@/types/contest";
import { PageResponse } from "@/types/api";

export function getContests(page: number) {
  return apiRequest<PageResponse<ContestDetail>>(`/api/contests?page=${page}`);
}

export function getContest(id: number) {
  return apiRequest<ContestDetail>(`/api/contests/${id}`);
}
