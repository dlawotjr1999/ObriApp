// 임시 더미 데이터. 지원 API(GET /api/applications/me 등) 연동 시 제거.
// 셋 다 "내가 제출한 지원"이라 applicant는 매번 MOCK_USER(mocks/user.ts) 기준으로 채운다 —
// 실제로는 서버가 로그인 유저 정보를 applicant로 내려주는 값이라 지원자마다 달라지지만,
// mock 데이터엔 지원자가 한 명(나)뿐이라 다르게 넣을 이유가 없다.
import { ApplicationSummary } from "@/types/application";
import { MOCK_USER } from "@/mocks/user";

const MOCK_APPLICANT = {
  nickname: MOCK_USER.nickname,
  instrument: MOCK_USER.instrument,
  phoneNumber: MOCK_USER.phoneNumber,
  careers: MOCK_USER.careers,
};

export const MOCK_APPLICATIONS: ApplicationSummary[] = [
  {
    id: 1,
    post: {
      id: 2,
      title: "홍대 주말 버스킹 팀원 모집",
      category: "버스킹",
      eventAt: "2026-08-12T18:30:00",
      location: "서울 마포구 홍대 걷고싶은거리",
      status: "PARTIALLY_CLOSED",
    },
    applicant: MOCK_APPLICANT,
    additionalInfo: "주말마다 홍대에서 자주 연습하고 있어서 바로 합류 가능합니다!",
    status: "PENDING",
    createdAt: "2026-07-02T10:00:00",
  },
  {
    id: 2,
    post: {
      id: 3,
      title: "목관 합주 스터디 모집",
      category: "합주",
      eventAt: "2026-09-05T11:00:00",
      location: "경기 수원시 OO연습실",
      status: "OPEN",
    },
    applicant: MOCK_APPLICANT,
    additionalInfo: "가볍게 합주하면서 실력을 늘리고 싶어 지원합니다.",
    status: "ACCEPTED",
    createdAt: "2026-07-03T09:00:00",
  },
  {
    id: 3,
    post: {
      id: 5,
      title: "부산 현악 트리오 모집",
      category: "앙상블",
      eventAt: "2026-08-22T15:00:00",
      location: "부산 해운대구 OO연습실",
      status: "PARTIALLY_CLOSED",
    },
    applicant: MOCK_APPLICANT,
    status: "REJECTED",
    createdAt: "2026-06-30T08:00:00",
  },
];
