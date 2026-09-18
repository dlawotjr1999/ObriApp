// 임시 더미 데이터. 모집글 API(GET /api/posts, /api/posts/{id}) 연동 시 제거.
import { PostDetail } from "@/types/post";

export const MOCK_POSTS: PostDetail[] = [
  {
    id: 1,
    title: "현악 앙상블 신규 단원 모집",
    category: "앙상블",
    eventAt: "2026-08-01T14:00:00",
    location: "서울 강남구 OO스튜디오",
    instruments: [
      { instrument: "바이올린", currentPeople: 1, people: 2 },
      { instrument: "첼로", currentPeople: 0, people: 1 },
    ],
    status: "OPEN",
    writer: { nickname: "홍길동", instrument: "바이올린", mannerScore: 3.0 },
    timetable: "매주 토요일 오후 2시 합주",
    description: "정기 합주와 소규모 발표회를 목표로 하는 현악 앙상블입니다. 함께하실 바이올린·첼로 단원을 모집합니다.",
    createdAt: "2026-07-01T10:00:00",
  },
  {
    id: 2,
    title: "홍대 주말 버스킹 팀원 모집",
    category: "버스킹",
    eventAt: "2026-08-12T18:30:00",
    location: "서울 마포구 홍대 걷고싶은거리",
    instruments: [
      { instrument: "바이올린", currentPeople: 2, people: 2 },
      { instrument: "비올라", currentPeople: 0, people: 1 },
      { instrument: "첼로", currentPeople: 1, people: 1 },
    ],
    status: "PARTIALLY_CLOSED",
    writer: { nickname: "김연주", instrument: "첼로", mannerScore: 4.5 },
    timetable: "리허설 (17:30), 버스킹 (18:30~20:00)",
    description: "주말 저녁 홍대 거리에서 함께 연주할 팀원을 찾습니다. 가벼운 클래식·영화음악 위주로 합주해요.",
    createdAt: "2026-07-02T09:00:00",
  },
  {
    id: 3,
    title: "목관 합주 스터디 모집",
    category: "합주",
    eventAt: "2026-09-05T11:00:00",
    location: "경기 수원시 OO연습실",
    instruments: [
      { instrument: "플루트", currentPeople: 0, people: 1 },
      { instrument: "피아노", currentPeople: 0, people: 1 },
    ],
    status: "OPEN",
    writer: { nickname: "이성악", instrument: "피아노", mannerScore: 4.8 },
    timetable: "매주 금요일 오전 11시",
    description: "목관·피아노 위주로 가볍게 합주하며 실력을 늘리는 스터디입니다.",
    createdAt: "2026-07-03T08:30:00",
  },
  {
    id: 4,
    title: "가을 정기연주회 세션 모집",
    category: "연주회",
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
    description: "가을 정기연주회를 준비할 현악 세션원을 모집합니다. 악보는 사전 공유돼요.",
    createdAt: "2026-07-04T07:00:00",
  },
  {
    id: 5,
    title: "부산 현악 트리오 모집",
    category: "앙상블",
    eventAt: "2026-08-22T15:00:00",
    location: "부산 해운대구 OO연습실",
    instruments: [
      { instrument: "바이올린", currentPeople: 1, people: 1 },
      { instrument: "첼로", currentPeople: 0, people: 1 },
      { instrument: "피아노", currentPeople: 0, people: 1 },
    ],
    status: "PARTIALLY_CLOSED",
    writer: { nickname: "최바이", instrument: "바이올린", mannerScore: 3.9 },
    timetable: "매주 일요일 오후 3시",
    description: "정기 모임으로 이어갈 현악 트리오 단원을 모집합니다.",
    createdAt: "2026-06-30T11:00:00",
  },
];

export function getMockPostById(id: number): PostDetail | undefined {
  return MOCK_POSTS.find((post) => post.id === id);
}
