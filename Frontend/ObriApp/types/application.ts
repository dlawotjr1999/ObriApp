import { PostStatus } from "./post";

export type ApplicationStatus =
  | "PENDING"
  | "ACCEPTED"
  | "REJECTED"
  | "CANCELLED"
  | "REVOKED";

export interface ApplicationSummary {
  id: number;
  post: {
    id: number;
    title: string;
    category: string;
    eventAt: string;
    location: string;
    status: PostStatus;
  };
  status: ApplicationStatus;
  createdAt: string;
}
