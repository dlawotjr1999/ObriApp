// 임시 더미 데이터. 구인글 API(GET /api/posts, /api/posts/{id}) 연동 시 제거.
import { PostDetail } from "@/types/post";

export const MOCK_POSTS: PostDetail[] = [
  {
    id: 1,
    title: "결혼식 바이올린 구인",
    category: "결혼",
    eventAt: "2026-08-01T14:00:00",
    location: "서울 강남구 OO웨딩홀",
    instruments: [
      { instrument: "바이올린", currentPeople: 1, people: 2 },
      { instrument: "첼로", currentPeople: 0, people: 1 },
    ],
    status: "OPEN",
    writer: { nickname: "홍길동", instrument: "바이올린", mannerScore: 3.0 },
    timetable: "리허설 1회 (13:00), 본식 (14:00)",
    pay: 150000,
    description: "결혼식 축가 및 입퇴장 연주를 함께해 주실 분을 찾습니다.",
    createdAt: "2026-07-01T10:00:00",
  },
  {
    id: 2,
    title: "기업 행사 현악 4중주",
    category: "행사",
    eventAt: "2026-08-12T18:30:00",
    location: "서울 중구 OO호텔 그랜드볼룸",
    instruments: [
      { instrument: "바이올린", currentPeople: 2, people: 2 },
      { instrument: "비올라", currentPeople: 0, people: 1 },
      { instrument: "첼로", currentPeople: 1, people: 1 },
    ],
    status: "PARTIALLY_CLOSED",
    writer: { nickname: "김연주", instrument: "첼로", mannerScore: 4.5 },
    timetable: "사운드체크 (17:30), 본행사 (18:30~20:00)",
    pay: 250000,
    description: "기업 창립기념 만찬 배경 연주입니다. 정장 드레스코드.",
    createdAt: "2026-07-02T09:00:00",
  },
  {
    id: 3,
    title: "추모 음악회 플루트·피아노 구인",
    category: "추모",
    eventAt: "2026-09-05T11:00:00",
    location: "경기 수원시 OO성당",
    instruments: [
      { instrument: "플루트", currentPeople: 0, people: 1 },
      { instrument: "피아노", currentPeople: 0, people: 1 },
    ],
    status: "OPEN",
    writer: { nickname: "이성악", instrument: "피아노", mannerScore: 4.8 },
    timetable: "리허설 (10:00), 추모식 (11:00~12:00)",
    pay: 120000,
    description: "조용하고 경건한 분위기의 추모 음악회입니다.",
    createdAt: "2026-07-03T08:30:00",
  },
  {
    id: 4,
    title: "앨범 레코딩 현악 세션",
    category: "레코딩",
    eventAt: "2026-07-20T13:00:00",
    location: "서울 마포구 OO스튜디오",
    instruments: [
      { instrument: "바이올린", currentPeople: 0, people: 4 },
      { instrument: "비올라", currentPeople: 0, people: 2 },
      { instrument: "첼로", currentPeople: 0, people: 2 },
    ],
    status: "OPEN",
    writer: { nickname: "박작곡", instrument: "피아노", mannerScore: 4.2 },
    timetable: "세션 A (13:00~15:00), 세션 B (15:30~17:30)",
    pay: 200000,
    description: "인디 뮤지션 정규 앨범 레코딩 세션입니다. 악보는 사전 공유.",
    createdAt: "2026-07-04T07:00:00",
  },
  {
    id: 5,
    title: "부산 결혼식 트리오 구인",
    category: "결혼",
    eventAt: "2026-08-22T15:00:00",
    location: "부산 해운대구 OO웨딩",
    instruments: [
      { instrument: "바이올린", currentPeople: 1, people: 1 },
      { instrument: "첼로", currentPeople: 0, people: 1 },
      { instrument: "피아노", currentPeople: 0, people: 1 },
    ],
    status: "PARTIALLY_CLOSED",
    writer: { nickname: "최바이", instrument: "바이올린", mannerScore: 3.9 },
    timetable: "리허설 (14:00), 본식 (15:00~16:30)",
    pay: 180000,
    description: "클래식 편곡 위주 웨딩 연주입니다.",
    createdAt: "2026-06-30T11:00:00",
  },
];

export function getMockPostById(id: number): PostDetail | undefined {
  return MOCK_POSTS.find((post) => post.id === id);
}
