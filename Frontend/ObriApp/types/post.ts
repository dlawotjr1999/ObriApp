// 구인글(Post) 도메인 타입. 노션 API 스펙(GET /api/posts) 기준.
// 화면·컴포넌트·더미데이터가 공유하는 단일 소스.

export type PostStatus = "OPEN" | "CLOSED";

export interface PostInstrument {
  instrument: string;
  people: number;
}

// 리스트(전체 조회)에서 카드로 보여줄 때 필요한 최소 필드.
// 단건 조회의 writer / timetable 등은 PostDetail에서 확장.
export interface PostSummary {
  id: number;
  title: string;
  category: string;
  eventAt: string; // ISO 8601 (예: "2024-05-01T14:00:00")
  location: string;
  instruments: PostInstrument[];
  status: PostStatus;
}

export interface PostWriter {
  nickname: string;
  instrument: string;
  mannerScore: number;
}

// 단건 조회(GET /api/posts/{id}) 응답. 상세 화면에서 사용.
export interface PostDetail extends PostSummary {
  writer: PostWriter;
  timetable: string;
  pay: number;
  description?: string;
  createdAt: string;
}
