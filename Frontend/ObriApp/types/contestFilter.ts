export interface ContestFilter {
  sort: "deadline" | "default";
  categories: string[];
  instruments: string[];
}

export const DEFAULT_CONTEST_FILTER: ContestFilter = {
  sort: "default",
  categories: [],
  instruments: [],
};
