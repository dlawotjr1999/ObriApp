// 임시 더미 데이터. 구인글 API(GET /api/posts, /api/posts/{id}) 연동 시 제거.
// 리스트 화면과 상세 화면이 공유하는 단일 소스.

import { PostDetail } from "@/types/post";

export const MOCK_POSTS: PostDetail[] = [
  {
    id: 1,
    title: "결혼식 바이올린 구인",
    category: "결혼",
    eventAt: "2024-05-01T14:00:00",
    location: "서울 강남구 OO웨딩홀",
    instruments: [
      { instrument: "바이올린", people: 2 },
      { instrument: "첼로", people: 1 },
    ],
    status: "OPEN",
    writer: {
      nickname: "홍길동",
      instrument: "바이올린",
      mannerScore: 3.0,
    },
    timetable: "리허설 1회 (13:00), 본식 (14:00)",
    pay: 150000,
    description:
      "결혼식 축가 및 입퇴장 연주를 함께해 주실 분을 찾습니다. 클래식 위주이며, 곡 리스트는 확정 후 공유드립니다.",
    createdAt: "2024-01-01T00:00:00",
  },
  {
    id: 2,
    title: "기업 행사 현악 4중주",
    category: "행사",
    eventAt: "2024-06-12T18:30:00",
    location: "서울 중구 OO호텔 그랜드볼룸",
    instruments: [
      { instrument: "바이올린", people: 2 },
      { instrument: "비올라", people: 1 },
      { instrument: "첼로", people: 1 },
    ],
    status: "OPEN",
    writer: {
      nickname: "김연주",
      instrument: "첼로",
      mannerScore: 4.5,
    },
    timetable: "사운드체크 (17:30), 본행사 (18:30~20:00)",
    pay: 250000,
    description:
      "기업 창립기념 만찬 행사 배경 연주입니다. 식전 30분 사운드체크 포함, 정장 드레스코드입니다.",
    createdAt: "2024-02-10T00:00:00",
  },
];

export function getMockPostById(id: number): PostDetail | undefined {
  return MOCK_POSTS.find((post) => post.id === id);
}
