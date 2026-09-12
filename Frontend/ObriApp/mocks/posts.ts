// 임시 더미 데이터. 모집글 API(GET /api/posts, /api/posts/{id}) 연동 시 제거.
// 필드 구성은 types/post.ts(백엔드 DTO 1:1 대응)를 그대로 따른다 — isMine/hasApplied/applicationCount는
// 실제로는 서버가 로그인 유저 기준으로 계산해 내려주는 값이라, mock 데이터 상에선 "현재 로그인한
// 목데이터 유저(mocks/user.ts MOCK_USER, id 1)" 기준으로 미리 계산해 넣어둔 값이다.
import { PostDetail } from "@/types/post";

export const MOCK_POSTS: PostDetail[] = [
  {
    id: 1,
    title: "현악 앙상블 신규 단원 모집",
    category: "앙상블",
    eventAt: "2026-08-01T14:00:00",
    location: "서울 강남구 OO스튜디오",
    region: "서울",
    instruments: [
      { id: 11, instrument: "바이올린", people: 2, confirmed: 1, closed: false },
      { id: 12, instrument: "첼로", people: 1, confirmed: 0, closed: false },
    ],
    status: "OPEN",
    writer: { nickname: "홍길동", instrument: "바이올린" },
    timetable: "매주 토요일 오후 2시 합주",
    description: "정기 합주와 소규모 발표회를 목표로 하는 현악 앙상블입니다. 함께하실 바이올린·첼로 단원을 모집합니다.",
    createdAt: "2026-07-01T10:00:00",
    applicationCount: 0,
    isMine: true, // 내가 등록한 글(MY_POST_IDS)
    hasApplied: false,
  },
  {
    id: 2,
    title: "홍대 주말 버스킹 팀원 모집",
    category: "버스킹",
    eventAt: "2026-08-12T18:30:00",
    location: "서울 마포구 홍대 걷고싶은거리",
    region: "서울",
    instruments: [
      { id: 21, instrument: "바이올린", people: 2, confirmed: 2, closed: true },
      { id: 22, instrument: "비올라", people: 1, confirmed: 0, closed: false },
      { id: 23, instrument: "첼로", people: 1, confirmed: 1, closed: true },
    ],
    status: "PARTIALLY_CLOSED",
    writer: { nickname: "김연주", instrument: "첼로" },
    timetable: "리허설 (17:30), 버스킹 (18:30~20:00)",
    description: "주말 저녁 홍대 거리에서 함께 연주할 팀원을 찾습니다. 가벼운 클래식·영화음악 위주로 합주해요.",
    createdAt: "2026-07-02T09:00:00",
    applicationCount: 1,
    isMine: false,
    hasApplied: true, // MOCK_APPLICATIONS[0]이 이 글에 지원한 상태
  },
  {
    id: 3,
    title: "목관 합주 스터디 모집",
    category: "합주",
    eventAt: "2026-09-05T11:00:00",
    location: "경기 수원시 OO연습실",
    region: "경기",
    instruments: [
      { id: 31, instrument: "플루트", people: 1, confirmed: 0, closed: false },
      { id: 32, instrument: "피아노", people: 1, confirmed: 0, closed: false },
    ],
    status: "OPEN",
    writer: { nickname: "이성악", instrument: "피아노" },
    timetable: "매주 금요일 오전 11시",
    description: "목관·피아노 위주로 가볍게 합주하며 실력을 늘리는 스터디입니다.",
    createdAt: "2026-07-03T08:30:00",
    applicationCount: 1,
    isMine: false,
    hasApplied: true, // MOCK_APPLICATIONS[1]이 이 글에 지원한 상태
  },
  {
    id: 4,
    title: "가을 정기연주회 세션 모집",
    category: "연주회",
    eventAt: "2026-07-20T13:00:00",
    location: "서울 마포구 OO스튜디오",
    region: "서울",
    instruments: [
      { id: 41, instrument: "바이올린", people: 4, confirmed: 0, closed: false },
      { id: 42, instrument: "비올라", people: 2, confirmed: 0, closed: false },
      { id: 43, instrument: "첼로", people: 2, confirmed: 0, closed: false },
    ],
    status: "OPEN",
    writer: { nickname: "박작곡", instrument: "피아노" },
    timetable: "세션 A (13:00~15:00), 세션 B (15:30~17:30)",
    description: "가을 정기연주회를 준비할 현악 세션원을 모집합니다. 악보는 사전 공유돼요.",
    createdAt: "2026-07-04T07:00:00",
    applicationCount: 0,
    isMine: true, // 내가 등록한 글(MY_POST_IDS)
    hasApplied: false,
  },
  {
    id: 5,
    title: "부산 현악 트리오 모집",
    category: "앙상블",
    eventAt: "2026-08-22T15:00:00",
    location: "부산 해운대구 OO연습실",
    region: "부산",
    instruments: [
      { id: 51, instrument: "바이올린", people: 1, confirmed: 1, closed: true },
      { id: 52, instrument: "첼로", people: 1, confirmed: 0, closed: false },
      { id: 53, instrument: "피아노", people: 1, confirmed: 0, closed: false },
    ],
    status: "PARTIALLY_CLOSED",
    writer: { nickname: "최바이", instrument: "바이올린" },
    timetable: "매주 일요일 오후 3시",
    description: "정기 모임으로 이어갈 현악 트리오 단원을 모집합니다.",
    createdAt: "2026-06-30T11:00:00",
    applicationCount: 1,
    isMine: false,
    hasApplied: true, // MOCK_APPLICATIONS[2]이 이 글에 지원한 상태
  },
];

export function getMockPostById(id: number): PostDetail | undefined {
  return MOCK_POSTS.find((post) => post.id === id);
}
