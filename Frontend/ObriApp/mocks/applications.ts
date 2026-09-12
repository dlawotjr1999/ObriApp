import { ApplicationSummary } from "@/types/application";

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
    status: "REJECTED",
    createdAt: "2026-06-30T08:00:00",
  },
];
