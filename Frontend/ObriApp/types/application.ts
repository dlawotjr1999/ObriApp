// 지원(Application) 도메인 타입. 백엔드 AppResponseDTO/AppRequestDTO/ApplicantResponseDTO/
// ApplicationPostSummaryDTO 기준(backend/obri/.../application/dto)으로 필드를 1:1 맞춘다.
import { Career } from "./user";
import { PostStatus } from "./post";

// 지원 상태 — 전이는 행위자·출발 상태별로 고정(백엔드 ApplicationStatus, 명세 Application §2.4).
export type ApplicationStatus =
  | "PENDING"
  | "ACCEPTED"
  | "REJECTED"
  | "CANCELLED"
  | "REVOKED";

// 지원서 응답에 중첩되는 모집글 요약. 백엔드 ApplicationPostSummaryDTO와 1:1 대응(지원자가 소비).
export interface ApplicationPostSummary {
  id: number;
  title: string;
  category: string;
  eventAt: string;
  location: string;
  status: PostStatus;
}

// 지원서 응답에 중첩되는 지원자 프로필. 백엔드 ApplicantResponseDTO와 1:1 대응(모집자가 소비).
// email은 없음 — 모집자에게 지원자의 email이 노출되지 않도록 백엔드가 의도적으로 제외한 필드다.
export interface Applicant {
  nickname: string;
  instrument: string;
  phoneNumber: string;
  careers: Career[];
}

// 지원서 응답. 백엔드 AppResponseDTO와 1:1 대응 — 제출·단건 조회·지원자 목록·내 지원 목록
// 전 엔드포인트가 공통으로 쓴다. 모집자는 applicant를, 지원자는 post를 주로 소비한다
// (관점별 DTO 분리 없이 단일 타입으로 통일된 백엔드 설계를 그대로 따름).
export interface ApplicationSummary {
  id: number;
  post: ApplicationPostSummary;
  applicant: Applicant;
  additionalInfo?: string;
  status: ApplicationStatus;
  createdAt: string;
}

// 지원서 제출 요청 바디. 백엔드 AppRequestDTO와 1:1 대응.
// instrument 필드가 없다 — 지원 대상 악기는 선택형이 아니라 내 프로필 악기(User.instrument)로
// 서버가 자동 판정하기 때문(모집 목록에 없는 악기여도 자리만 미반영된 채 지원 자체는 허용됨).
export interface ApplicationCreateRequest {
  postId: number;
  additionalInfo?: string;
}
