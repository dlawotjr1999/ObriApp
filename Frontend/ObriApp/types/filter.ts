import { PostStatus } from "./post";

export interface PostFilter {
  sort: "latest" | "default";
  categories: string[];
  instruments: string[];
  regions: string[];
  status: PostStatus[];
  startDate?: string; // "YYYY-MM-DD", 백엔드 GET /api/posts의 startDate 파라미터와 동일
  endDate?: string; // "YYYY-MM-DD", 백엔드 GET /api/posts의 endDate 파라미터와 동일
}

export const DEFAULT_FILTER: PostFilter = {
  sort: "default",
  categories: [],
  instruments: [],
  regions: [],
  status: [],
  startDate: undefined,
  endDate: undefined,
};

