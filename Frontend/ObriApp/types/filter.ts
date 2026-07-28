import { PostStatus } from "./post";

export interface PostFilter {
  sort: "latest" | "default";
  categories: string[];
  instruments: string[];
  regions: string[];
  status: PostStatus[];
}

export const DEFAULT_FILTER: PostFilter = {
  sort: "default",
  categories: [],
  instruments: [],
  regions: [],
  status: [],
};

